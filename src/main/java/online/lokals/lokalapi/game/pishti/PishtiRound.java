package online.lokals.lokalapi.game.pishti;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public class PishtiRound {

    private List<Card> capturedCards;
    private List<Card> pishtis;

    public PishtiRound() {
        this.capturedCards = Collections.emptyList();
        this.pishtis = Collections.emptyList();
    }

    public void addPishti(Card cardFromStack, Card cardPlayed) {
        this.capturedCards.add(cardFromStack);
        this.pishtis.add(cardPlayed);
    }

    public void capture(List<Card> cardsFromStack) {
        this.capturedCards.addAll(cardsFromStack);
    }

    public int getScore() {
        int initialPoint = (cardCountEarning() + this.getPishtis().size() * 10);
        return Stream.of(this.getCapturedCards(), this.getPishtis())
                .flatMap(Collection::stream)
                .reduce(initialPoint, (subTotal, currentCard) -> subTotal + currentCard.pishtiValue(), Integer::sum);
    }

    private int cardCountEarning() {
        return ((this.getCapturedCards().size() + this.getPishtis().size()) > 26) ? 3 : 0;
    }

}
