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
        this.id = backgammon.getId();
        this.firstPlayer = backgammon.getFirstPlayer();
        this.secondPlayer = backgammon.getSecondPlayer();
        this.possibleMoves = backgammon.possibleMoves();
        this.turn = backgammon.currentTurn();

        if (backgammon.isGameOver()) {
            var mars = backgammon.getMultiply() == 2;
            this.report = new BackgammonReport(backgammon.getWinner().getId(), mars, backgammon.getStatus());
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