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

    private String id;
    private BackgammonSessionEventType type;
    private BackgammonSessionResponse backgammonSession;

    public static BackgammonSessionEvent sit(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(
                backgammonSession.getId(),
                BackgammonSessionEventType.SIT,
                new BackgammonSessionResponse(backgammonSession)
        );
    }

    public static BackgammonSessionEvent quit(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(
                backgammonSession.getId(),
                BackgammonSessionEventType.QUIT,
                new BackgammonSessionResponse(backgammonSession)
        );
    }

    public static BackgammonSessionEvent start(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(
                backgammonSession.getId(),
                BackgammonSessionEventType.START,
                new BackgammonSessionResponse(backgammonSession)
        );
    }
    
    public static BackgammonSessionEvent end(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(
                backgammonSession.getId(),
                BackgammonSessionEventType.END,
                new BackgammonSessionResponse(backgammonSession)
        );
    }

    public static BackgammonSessionEvent firstDie(BackgammonSession backgammonSession) {
        return new BackgammonSessionEvent(
                backgammonSession.getId(),
                BackgammonSessionEventType.FIRST_DIE,
                new BackgammonSessionResponse(backgammonSession)
        );
    }
    
}
