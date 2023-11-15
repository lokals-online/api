package online.lokals.lokalapi.game.pishti;

import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;

import java.util.Objects;

@Getter
@Setter
public class PishtiSessionResponse {

    private String id;
    private Player home;
    private Player away;
    private PishtiSettings settings;
    private PishtiResponse currentMatch;
    private String status;
    private long homeScore;
    private long awayScore;

    public PishtiSessionResponse(PishtiSession pishtiSession, Player player) {
        this.id = pishtiSession.getId();
        this.home = pishtiSession.getHome();
        this.away = pishtiSession.getAway();
        this.settings = pishtiSession.getSettings();
        if (Objects.equals(pishtiSession.getStatus(), PishtiSessionStatus.STARTED)) {
            Pishti pishti = pishtiSession.getMatches().get(pishtiSession.getMatches().size() - 1);
            this.currentMatch = new PishtiResponse(pishti, player);

            this.homeScore = pishtiSession.getHomeScore();
            this.awayScore = pishtiSession.getAwayScore();
        }
        this.status = pishtiSession.getStatus().name();
    }
}
