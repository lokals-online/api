package online.lokals.lokalapi.game.backgammon.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.lokals.lokalapi.game.backgammon.BackgammonSession;
import online.lokals.lokalapi.game.backgammon.BackgammonSessionResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BackgammonSessionEvent {

    private String gameId;
    private BackgammonSessionEventType type;
    private BackgammonSessionResponse backgammonSession;

    public static BackgammonSessionEvent sit(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(backgammonSession.getId(), BackgammonSessionEventType.SIT, new BackgammonSessionResponse(backgammonSession));
    }

    public static BackgammonSessionEvent start(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(backgammonSession.getId(), BackgammonSessionEventType.START, new BackgammonSessionResponse(backgammonSession));
    }

    public static BackgammonSessionEvent firstDice(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(backgammonSession.getId(), BackgammonSessionEventType.FIRST_DICE, new BackgammonSessionResponse(backgammonSession));
    }
}
