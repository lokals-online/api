package online.lokals.lokalapi.game.backgammon;

import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.pishti.Pishti;
import online.lokals.lokalapi.game.pishti.PishtiResponse;

import java.util.Objects;

@Getter
@Setter
public class BackgammonSessionResponse {

    private String id;
    private Player home;
    private Player away;
    private BackgammonSettings settings;
    private BackgammonResponse currentMatch;
    private String status;
    private long homeScore;
    private long awayScore;
    private Integer homeFirstDice;
    private Integer awayFirstDice;

    public BackgammonSessionResponse(BackgammonSession backgammonSession) {
        this.id = backgammonSession.getId();
        this.home = backgammonSession.getHome();
        this.away = backgammonSession.getAway();
        this.homeFirstDice = backgammonSession.getHomeFirstDice();
        this.awayFirstDice = backgammonSession.getAwayFirstDice();
        this.settings = backgammonSession.getSettings();
        if (Objects.equals(backgammonSession.getStatus(), BackgammonSessionStatus.STARTED)) {
            Backgammon backgammon = backgammonSession.getMatches().get(backgammonSession.getMatches().size() - 1);
            this.currentMatch = new BackgammonResponse(backgammon);

            this.homeScore = backgammonSession.getScoreBoard().getOrDefault(home.getUsername(), 0L);
            this.awayScore = backgammonSession.getScoreBoard().getOrDefault(away.getUsername(), 0L);
        }
        this.status = backgammonSession.getStatus().name();
    }
}
