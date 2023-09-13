package online.lokals.lokalapi.game.backgammon.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.lokals.lokalapi.game.backgammon.Backgammon;
import online.lokals.lokalapi.game.backgammon.BackgammonResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BackgammonGameEvent {

    private String gameId;
    private BackgammonGameEventType type;
    private BackgammonResponse backgammon;

    public static BackgammonGameEvent start(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, BackgammonGameEventType.START, new BackgammonResponse(backgammon));
    }

    public static BackgammonGameEvent rollDice(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, BackgammonGameEventType.ROLL_DICE, new BackgammonResponse(backgammon));
    }

    public static BackgammonGameEvent turnHasChanged(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, BackgammonGameEventType.TURN, new BackgammonResponse(backgammon));
    }

    public static BackgammonGameEvent move(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, BackgammonGameEventType.MOVE, new BackgammonResponse(backgammon));
    }

    public static BackgammonGameEvent gameOver(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, BackgammonGameEventType.GAME_OVER, new BackgammonResponse(backgammon));
    }
}
