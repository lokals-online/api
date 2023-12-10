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
    private BatakResponse currentMatch;
    private Map<String, Integer> scores;
    
    public BatakSessionResponse(BatakSession batakSession, Player player) {
        this.id = batakSession.getId();
        this.players = batakSession.getPlayers();
        this.settings = batakSession.getSettings();
        this.currentMatch = batakSession.getCurrentMatch() != null ? new BatakResponse(batakSession.getCurrentMatch(), player) : null;
        this.scores = batakSession.getScores();
    }

}