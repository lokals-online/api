package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.function.Predicate.not;

@Slf4j
@Getter
@Setter
public class Turn {

    @NotNull
    @JsonBackReference
    private Player player;

    private Integer[] dices;

    @Transient
    private int moveCount;

    private List<BackgammonMove> moves = new ArrayList<>();

    public static Turn first(@NotNull Player player, Integer[] dices) {
        Turn turn = new Turn(player);
        turn.setDices(dices);
        return turn;
    }

    public Turn(@NotNull Player player) {
        this.player = player;
    }

    public int getMoveCount() {
        if (Objects.nonNull(dices)) {
            return dices[0].equals(dices[1]) ? 4 : 2;
        }
        else {
            return 0;
        }
    }

    public void addMove(BackgammonMove backgammonMove) {
        backgammonMove.setMovedAt(System.currentTimeMillis());
        this.moves.add(backgammonMove);
    }

    @JsonIgnore
    public Integer[] rollDice() {
//        this.dices = new Integer[]{3, 2};
        this.dices = new Integer[]{(int) (Math.random() * 6 + 1), (int) (Math.random() * 6 + 1)};

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
        log.info(playedDices.toString());
        if (playedDices.isEmpty()) return dices;

        if (isDoubleDice()) {
            final Integer[] remainingDices = new Integer[4-playedDices.size()];
            Arrays.fill(remainingDices, dices[0]);
            return remainingDices;
        }
        else if (playedDices.size() == 1) {
            if (dices[0].equals(playedDices.get(0))) {
                return new Integer[] {dices[1]};
            }
            else if (dices[1].equals(playedDices.get(0))) {
                return new Integer[] {dices[0]};
            }

            return new Integer[] {dices[0] > dices[1] ? dices[1] : dices[0]};
        }
        else {
            return dices;
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
        return "Current Turn{" + player.getUsername() + "}";
    }
}
