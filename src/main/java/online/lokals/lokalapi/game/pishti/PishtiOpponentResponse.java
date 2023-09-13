package online.lokals.lokalapi.game.pishti;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PishtiOpponentResponse {
    private String username;
    private int hand;
    private List<Card> capturedCards;
    private List<Card> pishtis;
    private int score;

    public PishtiOpponentResponse(PishtiPlayer pishtiOpponent) {
        this(
            pishtiOpponent.getPlayerUsername(),
            pishtiOpponent.getHand().size(),
            pishtiOpponent.getCapturedCards(),
            pishtiOpponent.getPishtis(),
            pishtiOpponent.getScore()
        );
    }
}
