package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.Move;
import online.lokals.lokalapi.game.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;

// TODO: record?!!!
@Getter
public class BackgammonResponse {

    private final String id;
    private final BackgammonPlayer firstPlayer;
    private final BackgammonPlayer secondPlayer;
    private final BackgammonReport report;
    @Nullable private final Turn turn;
    private final Set<BackgammonMove> possibleMoves;

    public BackgammonResponse(Backgammon backgammon) {
        this.id = backgammon.getId();
        this.firstPlayer = backgammon.getFirstPlayer();
        this.secondPlayer = backgammon.getSecondPlayer();
        this.turn = backgammon.getTurn();
        this.possibleMoves = backgammon.possibleMoves();

        if (backgammon.isGameOver()) {
            this.report = new BackgammonReport(backgammon.getWinner().getId(), backgammon.isMars(), backgammon.getStatus());
        }
        else {
            this.report = new BackgammonReport(null, null, backgammon.getStatus());
        }
    }

    @JsonProperty("name")
    public String getName() {
        return firstPlayer.getUsername() + " vs " + secondPlayer.getUsername();
    }

}

record BackgammonReport(@Nullable String winner, @Nullable Boolean isMars, BackgammonStatus status) {}