package online.lokals.lokalapi.game.batak.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.batak.BatakSession;
import online.lokals.lokalapi.game.batak.api.BatakSessionResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BatakSessionEvent {

    private String id;
    private BatakSessionEventType type;
    private BatakSessionResponse batakSession;

    public static BatakSessionEvent sit(BatakSession batakSession) {
        return new BatakSessionEvent(
                batakSession.getId(),
                BatakSessionEventType.SIT,
                new BatakSessionResponse(batakSession)
        );
    }

    public static BatakSessionEvent start(BatakSession batakSession) {
        return new BatakSessionEvent(
                batakSession.getId(),
                BatakSessionEventType.START,
                new BatakSessionResponse(batakSession)
        );
    }

    public static BatakSessionEvent bet(BatakSession batakSession) {
        return new BatakSessionEvent(
                batakSession.getId(),
                BatakSessionEventType.BET,
                new BatakSessionResponse(batakSession)
        );
    }

    public static BatakSessionEvent quit(BatakSession batakSession) {
        return new BatakSessionEvent(
                batakSession.getId(),
                BatakSessionEventType.QUIT,
                new BatakSessionResponse(batakSession)
        );
    }

    public static BatakSessionEvent end(BatakSession batakSession) {
        return new BatakSessionEvent(
                batakSession.getId(),
                BatakSessionEventType.END,
                new BatakSessionResponse(batakSession)
        );
    }

}
