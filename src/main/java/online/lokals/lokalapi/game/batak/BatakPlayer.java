package online.lokals.lokalapi.game.batak;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.card.Card;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class BatakPlayer extends Player {
    
    private List<Card> hand = new ArrayList<>(13);

    private List<Integer> wonTricks = new ArrayList<>();

    @Nullable
    private Integer bid;
    
    public BatakPlayer(Player player, List<Card> hand) {
        super(player.getId(), player.getUsername(), player.isAnonymous());

        this.hand = hand.stream()
            .sorted(Comparator.comparing(Card::getType).thenComparing(Card::getNumber))
            .toList();
    }

    public void remove(Card card) {
        this.hand.remove(card);
    }

    public void addWon(int trickIndex) {
        this.wonTricks.add(trickIndex);
    }

    public Integer getScore() {
        return Objects.isNull(wonTricks) ? 0 : wonTricks.size();
    }

    public void bid(int betValue) {
        this.bid = betValue;
    }

    @Override
    public String toString() {
        return "BatakPlayer{" +
                "hand=" + hand.size() +
                "wonTricks=" + wonTricks.size() +
                ", bid=" + bid +
                '}';
    }

    public boolean fold() {
        return Objects.nonNull(bid) && bid == 0;
    }
}
