package online.lokals.lokalapi.game.backgammon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackgammonTableEvent {

    private String tableId;
    private String sessionId;
    private String home;
    private String away;
    private int homeScore;
    private int awayScore;

    public static BackgammonTableEvent created(BackgammonSession backgammonSession) {

        var away = (backgammonSession.getAway() != null) ? backgammonSession.getAway().getUsername() : "";

        return new BackgammonTableEvent(
                "backgammon",
                backgammonSession.getId(),
                backgammonSession.getHome().getUsername(),
                away,
                backgammonSession.getHomeScore(),
                backgammonSession.getAwayScore()
        );
    }

}
