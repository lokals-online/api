package online.lokals.lokalapi.game.batak.api;

import lombok.Getter;
import online.lokals.lokalapi.game.batak.BatakPlayer;

import java.util.Objects;

@Getter
public class BatakPlayerResponse {

    private final String id;
    private final String username;
    private final Integer bid;
    private final int score;

    public BatakPlayerResponse(String id, String username, Integer bid, int score) {
        this.id = id;
        this.username = username;
        this.bid = bid;
        this.score = score;
    }

    public BatakPlayerResponse(BatakPlayer p) {
        this.id = p.getId();
        this.username = p.getUsername();
        this.bid = p.getBid();
        this.score = Objects.nonNull(p.getScore()) ? p.getScore() : 0;
    }
}
