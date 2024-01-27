package online.lokals.lokalapi.game.batak.api;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.batak.BatakSession;
import online.lokals.lokalapi.game.batak.BatakSettings;

@Getter
@Setter
public class BatakSessionResponse {
    
    private String id;
    private List<Player> players;
    private BatakSettings settings; 
    private String currentMatchId;
    private Map<String, Integer> scores;
    private String status;
    
    public BatakSessionResponse(BatakSession batakSession) {
        this.id = batakSession.getId();
        this.players = batakSession.getPlayers();
        this.settings = batakSession.getSettings();
        this.currentMatchId = batakSession.getCurrentMatch() != null ? batakSession.getCurrentMatch().getId() : null;
        this.scores = batakSession.getScores();
        this.status = batakSession.getStatus().name();
    }

}