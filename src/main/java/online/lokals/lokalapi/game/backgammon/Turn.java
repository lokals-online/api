package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.function.Predicate.not;

@Getter
@Setter
public class Turn {

    @NotNull
    @JsonBackReference
    private Player player;

    private Integer[] dices;

    @Setter(AccessLevel.PRIVATE)
    private Integer moveCount;

    private List<BackgammonMove> moves = new ArrayList<>();

    public Turn(@NotNull Player player) {
        this.player = player;
    }

    public void addMove(BackgammonMove backgammonMove) {
        backgammonMove.setMovedAt(System.currentTimeMillis());
        this.moves.add(backgammonMove);
    }

    @JsonIgnore
    public Integer[] rollDice() {
//        this.dices = new Integer[]{3, 2};
        this.dices = new Integer[]{(int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)};

        if (dices[0].equals(dices[1])) {
            moveCount = 4;
        }
        else {
            moveCount = 2;
        }

        return dices;
    }

    @JsonIgnore
    public boolean dicePlayed() {
        return dices != null;
    }

    @JsonIgnore
    public boolean isDoubleDice() {
        return (this.dices[0].equals(this.dices[1]));
    }

    public Integer[] getRemainingDices() {
        if (dices == null || dices.length == 0) return null;

        List<Integer> playedDices = moves.stream().map(BackgammonMove::getDice).toList();
        if (playedDices.isEmpty()) return dices;

        if (isDoubleDice()) {
            final Integer[] remainingDices = new Integer[4-playedDices.size()];
            Arrays.fill(remainingDices, dices[0]);
            return remainingDices;
        }
        else {
            return Arrays.stream(dices).filter(not(playedDices::contains)).toArray(Integer[]::new);
        }
    }

    public String getPlayerId() {
        return player.getId();
    }

    @JsonIgnore
    public boolean isOver() {
        if (!dicePlayed()) return false;

        if (dices[0].equals(dices[1])) {
            return moves.size() == 4;
        }
        else {
            return moves.size() == 2;
        }
    }

    @Override
    public String toString() {
        return "Current Turn{playerId=" + player.toString() + "}";
    }
}
