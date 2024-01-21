package online.lokals.lokalapi.game.batak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.card.CardType;

@Getter
@Setter
@NoArgsConstructor
public class BatakBid {

    private String playerId;

    private int value;

    private CardType trump;

    public BatakBid(String playerId, Integer value) {
        this.playerId = playerId;
        this.value = value;
    }

}
