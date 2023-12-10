package online.lokals.lokalapi.game.backgammon;

import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class BackgammonSessionResponse {

    private String id;
    private Player home;
    private Player away;
    private BackgammonSettings settings;
    private BackgammonResponse currentMatch;
    private List<BackgammonResponse> matches;
    private String status;
    private int homeScore;
    private int awayScore;
    private Integer homeFirstDice;
    private Integer awayFirstDice;

    public BackgammonSessionResponse(BackgammonSession backgammonSession) {
        this.id = backgammonSession.getId();
        this.home = backgammonSession.getHome();
        this.away = backgammonSession.getAway();
        this.homeFirstDice = backgammonSession.getHomeFirstDice();
        this.awayFirstDice = backgammonSession.getAwayFirstDice();
        this.settings = backgammonSession.getSettings();
        if (Objects.equals(backgammonSession.getStatus(), BackgammonSessionStatus.STARTED) || 
        Objects.equals(backgammonSession.getStatus(), BackgammonSessionStatus.ENDED)) {
            Backgammon backgammon = backgammonSession.getMatches().get(backgammonSession.getMatches().size() - 1);
            this.currentMatch = new BackgammonResponse(backgammon);
            this.matches = backgammonSession.getMatches().stream().map(BackgammonResponse::new).toList();
        }
        this.homeScore = backgammonSession.getHomeScore();
        if (Objects.nonNull(away)) {
            this.awayScore = backgammonSession.getAwayScore();
        }
        else {
            this.awayScore = 0;
        }
        this.status = backgammonSession.getStatus().name();
    }
}
