package online.lokals.lokalapi.game.pishti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.card.Card;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class PishtiPlayer {

    @JsonIgnore
    private Player player;
    private List<Card> hand = new ArrayList<>(4);
    private List<PishtiRound> rounds = new ArrayList<>();

    public PishtiPlayer(Player player) {
        this.player = player;
        this.hand = Collections.emptyList();
        this.rounds = List.of(new PishtiRound());
    }

    public void played(Card card) {
        hand.remove(card);
    }

    public void pishti(Card cardFromStack, Card cardPlayed) {
        this.rounds
                .get(getCurrentSeriesIndex())
                .addPishti(cardFromStack, cardPlayed);
    }

    public void capture(List<Card> cardsFromStack) {
        this.rounds
                .get(getCurrentSeriesIndex())
                .capture(cardsFromStack);
    }

    void endRound() {
        this.rounds.add(new PishtiRound());
    }

    public String getId() {
        return this.getPlayer().getId();
    }

    public String getPlayerUsername() {
        return this.getPlayer().getUsername();
    }

    private int getCurrentSeriesIndex() {
        assert this.rounds != null;

        return this.rounds.size() - 1;
    }

    public List<Card> getCapturedCards() {
        return this.rounds.get(getCurrentSeriesIndex()).getCapturedCards();
    }

    public List<Card> getPishtis() {
        return this.rounds.get(getCurrentSeriesIndex()).getPishtis();
    }

    public int getScore() {
        return this.rounds.stream().map(PishtiRound::getScore).reduce(0, Integer::sum);
    }

}