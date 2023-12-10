package online.lokals.lokalapi.game.batak.api;

import lombok.Getter;
import online.lokals.lokalapi.game.batak.BatakPlayer;

@Getter
public class BatakPlayerResponse {

    private final String id;
    private final String username;
    private final Integer bid;

    public BatakPlayerResponse(String id, String username, Integer bid) {
        this.id = id;
        this.username = username;
        this.bid = bid;
    }

    public BatakPlayerResponse(BatakPlayer p) {
        this.id = p.getId();
        this.username = p.getUsername();
        this.bid = p.getBid();
    }
}
