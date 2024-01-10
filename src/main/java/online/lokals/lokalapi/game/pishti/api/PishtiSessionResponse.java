package online.lokals.lokalapi.game.pishti.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.pishti.Pishti;
import online.lokals.lokalapi.game.pishti.PishtiResponse;
import online.lokals.lokalapi.game.pishti.PishtiSession;
import online.lokals.lokalapi.game.pishti.PishtiSessionStatus;
import online.lokals.lokalapi.game.pishti.PishtiSettings;

import java.util.Objects;

@Getter
@Setter
public class PishtiSessionResponse {

    private String id;
    private PishtiPlayerResponse home;
    private PishtiPlayerResponse away;
    private PishtiSettings settings;
    private String currentMatchId;
    private String status;

    public PishtiSessionResponse(PishtiSession pishtiSession) {
        this.id = pishtiSession.getId();
        this.home = new PishtiPlayerResponse(pishtiSession.getHome(), pishtiSession.getHomeScore());
        this.away = new PishtiPlayerResponse(pishtiSession.getAway(), pishtiSession.getAwayScore());
        this.settings = pishtiSession.getSettings();
        if (Objects.equals(pishtiSession.getStatus(), PishtiSessionStatus.STARTED) &&
                Objects.nonNull(pishtiSession.getCurrentMatch())) {
            this.currentMatchId = pishtiSession.getCurrentMatch().getId();
        }

        this.status = pishtiSession.getStatus().name();
    }
}

@Data
class PishtiPlayerResponse {
    private String id;
    private String username;
    private Integer score;

    public PishtiPlayerResponse(Player player, Integer score) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.score = score;
    }
}