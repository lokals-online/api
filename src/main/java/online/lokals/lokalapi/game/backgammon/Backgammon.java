package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
@Document("backgammon_classic")
public class Backgammon implements Game {

    public static final int HIT_CHECKER_POINT_INDEX = 24;
    public static final int PICKING_POINT_INDEX = -1;

    @MongoId
    private String id;

    @NotNull
    private BackgammonPlayer firstPlayer;

    @NotNull
    private BackgammonPlayer secondPlayer;

    private List<Turn> turns;

    private BackgammonStatus status;

    private Player winner;

    private boolean mars;

    public Backgammon(Player player) {
        this.firstPlayer = new BackgammonPlayer(player);
    }

    public Backgammon(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = new BackgammonPlayer(firstPlayer);
        this.secondPlayer = new BackgammonPlayer(secondPlayer);
        this.turns = List.of(new Turn(firstPlayer));
    }

    public Backgammon(Player firstPlayer, Player secondPlayer, Integer[] dices, @Nullable String turnPlayerId) {
        this.firstPlayer = new BackgammonPlayer(firstPlayer);
        this.secondPlayer = new BackgammonPlayer(secondPlayer);
        
        var turnPlayer = ((turnPlayerId == null) || (Objects.equals(turnPlayerId, firstPlayer.getId()))) ? firstPlayer : secondPlayer;
        this.turns = List.of(Turn.first(turnPlayer, dices));
    }

    @Nonnull
    public String getTitle() {
        return Arrays.stream(getPlayers()).map(BackgammonPlayer::getUsername).collect(Collectors.joining(" vs. "));
    }

    @Nonnull
    @Override
    public String getName() {
        return "backgammon";
    }

    @Nonnull
    public BackgammonPlayer[] getPlayers() {
        return new BackgammonPlayer[]{firstPlayer, secondPlayer};
    }

    @Nullable
    public Turn currentTurn() {
        if (this.turns == null || this.turns.isEmpty()) return null;

        int size = this.turns.size();
        return this.turns.get(size-1);
    }


    @Nullable
    public BackgammonPlayer getPlayer(@Nonnull String playerId) {
        return (firstPlayer.getId().equals(playerId)) ? firstPlayer : secondPlayer;
    }

    @Nullable
    public BackgammonPlayer getOpponent(@Nonnull String playerId) {
        BackgammonPlayer[] players = getPlayers();

        if (players.length != 2) return null;

        if (Objects.equals(players[1].getPlayer().getId(), playerId)) {
            return players[0];
        }
        else {
            return players[1];
        }
    }

    public BackgammonStatus getStatus() {
        if (BackgammonStatus.ENDED.equals(this.status)) {
            return BackgammonStatus.ENDED;
        }

        else {
            return (this.turns == null || this.turns.isEmpty()) ? BackgammonStatus.STARTING : BackgammonStatus.STARTED;
        }

        // TODO: return BackgammonStatus.ENDED;
    }

    public void start() {
        assert (Objects.nonNull(firstPlayer) && Objects.nonNull(secondPlayer)) && Objects.nonNull(currentTurn());

        this.status = BackgammonStatus.STARTED;
    }

    public Integer[] rollDice() {
        assert currentTurn() != null;

        return Objects.requireNonNull(currentTurn()).rollDice();
    }

    public void move(@NotNull Player player, BackgammonMove move) {
        // validate
        Turn turn = currentTurn();

        BackgammonPlayer currentPlayer = getPlayer(player.getId());
        assert Objects.nonNull(turn) && currentPlayer.getId().equals(turn.getPlayerId());

        log.trace("player[{}] is moving move:[from: {}, to: {}]}", player, move.getFrom(), move.getTo());

        currentPlayer.move(move);
        BackgammonPlayer opponent = getOpponent(currentPlayer.getId());
        opponent.checkHit(move.getTo());

        turn.addMove(move);

        if (currentPlayer.hasFinished()) {
            this.winner = currentPlayer.getPlayer();
            this.mars = getOpponent(currentPlayer.getId()).totalCheckersCount() == 15;
            this.status = BackgammonStatus.ENDED;
        }
    }

    public boolean isTurnOver() {
        return Objects.requireNonNull(currentTurn()).isOver();
    }

    public boolean isGameOver() {
        return BackgammonStatus.ENDED.equals(this.status);
    }

    public Turn changeTurn() {
        Turn turn = firstPlayer.getId().equals(Objects.requireNonNull(currentTurn()).getPlayerId()) ? new Turn(secondPlayer.getPlayer()) : new Turn(firstPlayer.getPlayer());
        this.turns.add(turn);

        return turn;
    }

    @Nullable
    public Set<BackgammonMove> possibleMoves() {
        Turn turn = currentTurn();

        if (Objects.isNull(turn) || !turn.dicePlayed()) return null;

        final HashSet<BackgammonMove> moves = new HashSet<>();
        BackgammonPlayer currentPlayer = getPlayer(turn.getPlayerId());
        BackgammonPlayer opponent = getOpponent(turn.getPlayerId());

        if (currentPlayer == null || opponent == null) {
            return null;
        }

        Integer[] remainingDices = turn.getRemainingDices();

        if (remainingDices.length == 0) {
            return Collections.emptySet();
        }

        if (currentPlayer.getHitCheckers() > 0) {
            for (Integer dice : remainingDices) {
                if (opponent.isTargetPointAvailable(HIT_CHECKER_POINT_INDEX -dice)) {
                    moves.add(new BackgammonMove(24, 24-dice));
                }
            }

            return moves;
        }
        else {
            for (Integer dice : remainingDices) {
                currentPlayer.getCheckers().keySet().stream()
                        .filter(integer -> ((integer - dice) >= 0 && opponent.isTargetPointAvailable(integer - dice)))
                        .map(integer -> new BackgammonMove(integer, integer - dice))
                        .collect(Collectors.toCollection(() -> moves));

                if (currentPlayer.isPicking()) {
                    // find exact slot that matches the dice
                    Integer checkers = currentPlayer.getCheckers(dice - 1);
                    if (checkers > 0) moves.add(new BackgammonMove(dice - 1, -1));

                    // checking points greater than dice
                    if (!currentPlayer.hasGreaterOrEqualCheckers(dice)) {
                        // if there is not any then find the greatest pickable point
                        currentPlayer.getCheckers().keySet().stream()
                                .max(Comparator.comparing(Integer::valueOf))
                                .ifPresent(greatestPickablePoint -> moves.add(new BackgammonMove(greatestPickablePoint, -1)));
                    }
                }
            }
        }

        return moves;
    }

    public void quitPlayer(Player player) {
        if (firstPlayer.getPlayer().equals(player)) {
            firstPlayer.setPlayer(null);
        }
        else {
            secondPlayer.setPlayer(null);
        }
    }
}