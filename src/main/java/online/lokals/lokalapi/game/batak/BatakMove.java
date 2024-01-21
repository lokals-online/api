package online.lokals.lokalapi.game.batak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.card.Card;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatakMove {
    private String playerId;
    private Card card;
}
