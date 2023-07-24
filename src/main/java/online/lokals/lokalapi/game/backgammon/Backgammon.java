package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import online.lokals.lokalapi.game.Player;

import java.util.*;
import java.util.stream.Collectors;

//@Document("backgammon_classic")
@Getter
public class Backgammon implements Game {

    public static final int HIT_SLOT_INDEX = 24;
    public static final int PICKING_SLOT_INDEX = -1;

//    @Id
    private String id;

    private BackgammonPlayer firstPlayer;

    private BackgammonPlayer secondPlayer;

    // TODO: private final List<Turn> turns = new ArrayList<>();
    private Turn turn;

    private BackgammonStatus status;

    private Player winner;
    private boolean mars;

    private Backgammon() {
    }

    public Backgammon(Player firstPlayer) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.firstPlayer = new BackgammonPlayer(firstPlayer);
    }

    public Backgammon(Player firstPlayer, Player secondPlayer) {
        this.id = String.valueOf(System.currentTimeMillis());
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

    public void setSecondPlayer(@Nonnull Player secondPlayer) {
        this.secondPlayer = new BackgammonPlayer(secondPlayer);
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
            return turn != null ? BackgammonStatus.STARTED : BackgammonStatus.STARTING;
        }

        // TODO: return BackgammonStatus.ENDED;
    }

    public void start() {
        assert Objects.nonNull(firstPlayer) && Objects.nonNull(secondPlayer);

        this.status = BackgammonStatus.STARTED;
//        this.turn = firstPlayer.getFirstDice() > secondPlayer.getFirstDice() ? new Turn(firstPlayer) : new Turn(secondPlayer);
    }

    public int firstDice(String playerId) {
        BackgammonPlayer player = getPlayer(playerId);
        int dice = (int) (Math.random() * 6 + 1);
        player.setFirstDice(dice);
        return dice;
    }

    public Integer[] rollDice() {
        return Objects.requireNonNull(this.turn).rollDice();
    }

    public void move(@NotNull String playerId, BackgammonMove move) {
        // validate

        BackgammonPlayer currentPlayer = getPlayer(playerId);

        currentPlayer.move(move);
        getOpponent(playerId).checkHit(move.to());

        turn.addMove(move);

        if (currentPlayer.hasFinished()) {
            this.winner = currentPlayer;
            this.mars = getOpponent(currentPlayer.getId()).getPieceCount() == 15;
            this.status = BackgammonStatus.ENDED;
        }
    }

    public boolean isTurnOver() {
        return turn.isOver();
    }

    public boolean isGameOver() {
        return BackgammonStatus.STARTED.equals(this.status) && (firstPlayer.hasFinished() || secondPlayer.hasFinished());
    }

    public void changeTurn() {
        this.turn = firstPlayer.equals(turn.getPlayer()) ? new Turn(secondPlayer) : new Turn(firstPlayer);
    }

    public boolean isReadyToPlay() {
        return getPlayers().stream().allMatch(BackgammonPlayer::firstDicePlayed);
    }

    @Nullable
    public Set<BackgammonMove> possibleMoves() {
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