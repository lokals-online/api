package online.lokals.lokalapi.game.backgammon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BackgammonGameEvent {

    private final String gameId;
    private final Object payload;


    public static BackgammonGameEvent start(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, backgammon);
    }

    public static BackgammonGameEvent turnHasChanged(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, backgammon);
    }

    public static BackgammonGameEvent gameOver(String gameId, Backgammon backgammon) {
        return new BackgammonGameEvent(gameId, backgammon);
    }
}
