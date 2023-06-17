package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Turn {

//    @NotNull
//    private Integer index;

    private String playerId;

    private Integer[] dices;

    @Setter(AccessLevel.PRIVATE)
    private Integer moveCount;

    // TODO: set initial capacity?
    private List<BackgammonMove> moves = new ArrayList<>();

    public Turn(String playerId) {
        this.playerId = playerId;
    }

    static Turn firstTurn(String playerId) {
        return new Turn(playerId);
    }

    public void addMove(BackgammonMove backgammonMove) {
        this.moves.add(backgammonMove);
    }

    @JsonIgnore
    public Integer[] rollDice() {
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
    public boolean isOver() {
        int diceTotal = moves.stream().mapToInt(BackgammonMove::getDice).reduce(0, Integer::sum);
        if (dices[0].equals(dices[1])) {
            return diceTotal == dices[0] * 4;
        }
        else {
            return diceTotal == dices[0] + dices[1];
        }
    }

    @Override
    public String toString() {
        return "Current Turn{playerId=" + playerId + "}";
    }
}
