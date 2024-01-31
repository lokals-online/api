package online.lokals.lokalapi.game.backgammon;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class BackgammonSessionResponse {

    private String id;
    private BackgammonPlayerResponse home;
    private BackgammonPlayerResponse away;
    private BackgammonSettings settings;
    private BackgammonResponse currentMatch;
    private List<BackgammonResponse> matches;
    private String status;

    public BackgammonSessionResponse(BackgammonSession backgammonSession) {
        this.id = backgammonSession.getId();
        this.home = new BackgammonPlayerResponse(
                backgammonSession.getHome().toPlayer(),
                backgammonSession.getHomeScore(),
                backgammonSession.getHomeFirstDice()
        );
        if (Objects.nonNull(backgammonSession.getAway())) {
            this.away = new BackgammonPlayerResponse(
                    backgammonSession.getAway().toPlayer(),
                    backgammonSession.getAwayScore(),
                    backgammonSession.getAwayFirstDice()
            );
        }

        this.settings = backgammonSession.getSettings();
        this.status = backgammonSession.getStatus().name();

        if (
                Objects.equals(backgammonSession.getStatus(), BackgammonSessionStatus.STARTED) ||
                Objects.equals(backgammonSession.getStatus(), BackgammonSessionStatus.ENDED)
        ) {
            Backgammon backgammon = backgammonSession.getMatches().get(backgammonSession.getMatches().size() - 1);
            this.currentMatch = new BackgammonResponse(backgammon);
            this.matches = backgammonSession.getMatches().stream().map(BackgammonResponse::new).toList();
        }

    }
}

@Data
class BackgammonPlayerResponse {
    private String id;
    private String username;
    private Integer score;
    private Integer firstDie;

    public BackgammonPlayerResponse(Player player, Integer score, Integer firstDie) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.score = score;
        this.firstDie = firstDie;
    }
}
