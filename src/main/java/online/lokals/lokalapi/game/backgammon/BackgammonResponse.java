package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

// TODO: record?!!!
@Getter
public class BackgammonResponse {

    private final String id;
    private final String[] players;
    private final Slot[] slots;
    @Nullable private final Slot player1HitSlot;
    @Nullable private final Slot player2HitSlot;
    @Nullable private final Turn turn;

    public BackgammonResponse(Backgammon backgammon) {
        this.id = backgammon.getId();
        this.players = new String[] {backgammon.getPlayers()[0].getUsername(), backgammon.getPlayers()[1].getUsername()};
        this.slots = backgammon.getBoard();
        this.turn = backgammon.currentTurn();
        this.player1HitSlot = backgammon.getFirstPlayerHitSlot();
        this.player2HitSlot = backgammon.getSecondPlayerHitSlot();
    }

    @JsonProperty("name")
    public String getName() {
        if (players.length == 2) {
            return players[0] + " vs " + players[1];
        }
        else if (players.length == 1) {
            return players[0] + " vs ?";
        }
        else {
            return "? vs ?";
        }
    }
}
