package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Getter;
import java.util.Set;

@Getter
public class BackgammonResponse {

    private final String id;
    private final BackgammonPlayer firstPlayer;
    @Nullable private final BackgammonPlayer secondPlayer;
    private final BackgammonReport report;
    @Nullable private final Turn turn;
    private final Set<BackgammonMove> possibleMoves;

    public BackgammonResponse(Backgammon backgammon) {
        Turn turn1;
        this.id = backgammon.getId();
        this.firstPlayer = backgammon.getFirstPlayer();
        this.secondPlayer = backgammon.getSecondPlayer();
        this.possibleMoves = backgammon.possibleMoves();

        try {
            turn1 = backgammon.currentTurn();
        } catch (Exception e) {
            turn1 = null;
        }

        this.turn = turn1;
        if (backgammon.isGameOver()) {
            this.report = new BackgammonReport(backgammon.getWinner().getId(), backgammon.isMars(), backgammon.getStatus());
        }
        else {
            this.report = new BackgammonReport(null, null, backgammon.getStatus());
        }
    }

    @JsonProperty("name")
    public String getName() {
        return firstPlayer.getUsername() + " vs " + (secondPlayer != null ? secondPlayer.getUsername() : "");
    }

}

record BackgammonReport(@Nullable String winner, @Nullable Boolean isMars, BackgammonStatus status) {}