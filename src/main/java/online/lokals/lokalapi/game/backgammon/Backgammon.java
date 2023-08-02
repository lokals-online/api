package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.lokals.lokalapi.game.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Document("backgammon_classic")
public class Backgammon implements Game {

    public static final int HIT_SLOT_INDEX = 24;
    public static final int PICKING_SLOT_INDEX = -1;

    @MongoId
    private String id;

    private BackgammonPlayer firstPlayer;

    private BackgammonPlayer secondPlayer;

    private List<Turn> turns;

    private BackgammonStatus status;

    private BackgammonPlayer winner;

    private boolean mars;

    public Backgammon(Player player) {
        this.firstPlayer = new BackgammonPlayer(player);
    }

    public Backgammon(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = new BackgammonPlayer(firstPlayer);
        this.secondPlayer = new BackgammonPlayer(secondPlayer);
    }

    @Nonnull
    public String getTitle() {
        return getPlayers().stream().map(BackgammonPlayer::getUsername).collect(Collectors.joining(" vs. "));
    }

    @Nonnull
    @Override
    public String getName() {
        return "backgammon";
    }

    @Override
    @Nonnull
    public List<BackgammonPlayer> getPlayers() {
        if (secondPlayer != null) {
            return List.of(firstPlayer, secondPlayer);
        }
        else {
            return List.of(firstPlayer);
        }
    }

    @Nullable
    public Turn currentTurn() {
        if (this.turns == null || this.turns.isEmpty()) return null;

        int size = this.turns.size();
        return this.turns.get(size-1);
    }


    public BackgammonPlayer getPlayer(@Nonnull String playerId) {
        return getPlayers().stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow();
    }

    public BackgammonPlayer getOpponent(@Nonnull String playerId) {
        return getPlayers().stream()
                .filter(player -> !player.getId().equals(playerId))
                .findFirst()
                .orElseThrow();
    }

    public BackgammonStatus getStatus() {
        if (BackgammonStatus.ENDED.equals(this.status)) {
            return BackgammonStatus.ENDED;
        }

        if (getPlayers().size() < 2) {
            return BackgammonStatus.WAITING_PLAYERS;
        }

        if (!isReadyToPlay()) {
            return BackgammonStatus.WAITING_FIRST_DICES;
        }
        else {
            return (this.turns == null || this.turns.isEmpty()) ? BackgammonStatus.STARTING : BackgammonStatus.STARTED;
        }

        // TODO: return BackgammonStatus.ENDED;
    }

    public void setSecondPlayer(@Nonnull Player secondPlayer) {
        this.secondPlayer = new BackgammonPlayer(secondPlayer);
    }

    public void start() {
        assert Objects.nonNull(firstPlayer) && Objects.nonNull(secondPlayer);

        this.status = BackgammonStatus.STARTED;
        this.turns = new ArrayList<>();
        Turn turn = firstPlayer.getFirstDice() > secondPlayer.getFirstDice() ? new Turn(firstPlayer.getPlayer()) : new Turn(secondPlayer.getPlayer());
        this.turns.add(turn);
    }

    public int firstDice(String playerId) {
        BackgammonPlayer player = getPlayer(playerId);
        int dice = (int) (Math.random() * 6 + 1);
        player.setFirstDice(dice);
        return dice;
    }

    public Integer[] rollDice() {
        assert currentTurn() != null;

        return Objects.requireNonNull(currentTurn()).rollDice();
    }

    public void move(@NotNull String playerId, BackgammonMove move) {
        // validate
        Turn turn = currentTurn();

        assert Objects.nonNull(turn) && playerId.equals(turn.getPlayerId());

        BackgammonPlayer currentPlayer = getPlayer(playerId);

        currentPlayer.move(move);
        getOpponent(playerId).checkHit(move.getTo());

        turn.addMove(move);

        if (currentPlayer.hasFinished()) {
            this.winner = currentPlayer;
            this.mars = getOpponent(currentPlayer.getId()).getPieceCount() == 15;
            this.status = BackgammonStatus.ENDED;
        }
    }

    public boolean isTurnOver() {
        return Objects.requireNonNull(currentTurn()).isOver();
    }

    public boolean isGameOver() {
        return BackgammonStatus.STARTED.equals(this.status) && (firstPlayer.hasFinished() || secondPlayer.hasFinished());
    }

    public void changeTurn() {
        Turn turn = firstPlayer.getId().equals(Objects.requireNonNull(currentTurn()).getPlayerId()) ? new Turn(secondPlayer.getPlayer()) : new Turn(firstPlayer.getPlayer());
        this.turns.add(turn);
    }

    public boolean isReadyToPlay() {
        return getPlayers().stream().allMatch(BackgammonPlayer::firstDicePlayed);
    }

    @Nullable
    public Set<BackgammonMove> possibleMoves() {
        Turn turn = currentTurn();

        if (Objects.isNull(turn) || !turn.dicePlayed()) return null;

        final HashSet<BackgammonMove> moves = new HashSet<>();
        BackgammonPlayer currentPlayer = getPlayer(turn.getPlayerId());
        BackgammonPlayer opponent = getOpponent(turn.getPlayerId());

        Integer[] remainingDices = turn.getRemainingDices();
        if (currentPlayer.getHitSlot().getCount() > 0) {
            for (Integer dice : remainingDices) {
                if (opponent.isTargetSlotAvailable(HIT_SLOT_INDEX-dice)) {
                    moves.add(new BackgammonMove(24, 24-dice));
                }
            }
        }
        else {
            for (Integer dice : remainingDices) {
                currentPlayer.getSlots().values().stream()
                        .filter(slot -> ((slot.getIndex() - dice) >= 0 && opponent.isTargetSlotAvailable(slot.getIndex() - dice)))
                        .map(slot -> new BackgammonMove(slot.getIndex(), slot.getIndex()-dice))
                        .collect(Collectors.toCollection(() -> moves));

                if (currentPlayer.isPicking()) {
                    // find exact slot that matches the dice
                    currentPlayer
                            .getSlot(dice - 1)
                            .filter(slot -> slot.getCount() > 0)
                            .ifPresent(slot -> moves.add(new BackgammonMove(slot.getIndex(), -1)));

                    // checking slots greater than dice
                    if (!currentPlayer.hasSlotsGreaterThanDice(dice)) {
                        // if there is not any then find the greatest pickable slot
                        currentPlayer.getSlots().keySet().stream()
                                .max(Comparator.comparing(Integer::valueOf))
                                .ifPresent(greatestPickableSlotIndex -> moves.add(new BackgammonMove(greatestPickableSlotIndex, -1)));
                    }
                }
            }
        }

        return moves;
    }
}

//  public void printBoard() {
//        IntStream.of(25,24,23,22,21,20,19).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.print("|||");
//        IntStream.of(18,17,16,15,14,13).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.println("\n\n\n");
//        IntStream.of(0,1,2,3,4,5,6).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.print("|||");
//        IntStream.of(7,8,9,10,11,12).forEach(i -> System.out.print(slots[i].toString()));
//        System.out.print("\n");
//  }