package online.lokals.lokalapi.game.batak;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.card.Card;
import online.lokals.lokalapi.game.card.CardType;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
@Document("batak")
public class Batak {

    public static final int MINIMUM_BID = 4;

    @MongoId
    private String id;

    private List<BatakPlayer> batakPlayers = new ArrayList<>();

    private Player dealer;

    // playerId
    private String turn;

    private BatakBid bid;

    private BatakStatus status;

    private List<BatakTrick> tricks;

    // starts with dealer that batakSession send
    public Batak(List<Player> players, int dealerIndex) {
        List<Card> cards = new ArrayList<Card>(Card.ofStandardPack());
        Collections.shuffle(cards);
        final Player firstTurnPlayer = players.get((dealerIndex+1) % 4);
        for (int i = 0; i < players.size(); i++) {
            var batakPlayer = new BatakPlayer(players.get(i), cards.subList(i*13, (i*13)+13));
            if (batakPlayer.getId().equals(firstTurnPlayer.getId())) {
                batakPlayer.bid(MINIMUM_BID);
                log.debug("player[{}] made the initial bid", firstTurnPlayer);
            }
            this.batakPlayers.add(batakPlayer);
        }

        this.turn = firstTurnPlayer.getId(); // player sitting next to dealer has the first turn!
        this.bid = new BatakBid(firstTurnPlayer.getId(), MINIMUM_BID);

        this.dealer = players.get(dealerIndex);
        this.status = BatakStatus.BIDDING;
        this.tricks = new ArrayList<>(13);

        log.debug("batak created: {}", this);
    }

    public BatakTrick getCurrentTrick() {
        if (Objects.nonNull(this.tricks) && !this.tricks.isEmpty()) {
            return this.tricks.get(this.tricks.size()-1);
        }
        else return null;
    }

    public void start() {
        this.status = BatakStatus.STARTED;
        this.turn = this.getBid().getPlayerId();
        this.tricks.add(new BatakTrick());
    }

    public void bid(Player player, Integer betValue) {
        getPlayer(player.getId()).bid(betValue);

        if (betValue > this.getBid().getValue()) {
            this.bid = new BatakBid(player.getId(), betValue);
        }
    }

    public void changeTurn() {
        int turnPlayerIndex = this.batakPlayers.indexOf(getPlayer(this.turn));
        log.debug("TURN INDEX => {}", turnPlayerIndex);
        this.turn = this.batakPlayers.get((turnPlayerIndex+1) % 4).getId();

        log.debug("turn has changed from[{}] to[{}]", turnPlayerIndex, ((turnPlayerIndex+1) % 4));
    }

    public void changeBidTurn() {
        int turnPlayerIndex = this.batakPlayers.indexOf(getPlayer(this.turn));
        log.debug("TURN INDEX => {}", turnPlayerIndex);

        for (int i = 1; i < batakPlayers.size() + 1; i++) {
            BatakPlayer nextPlayer = this.batakPlayers.get((turnPlayerIndex + i) % 4);
            if (!nextPlayer.fold()) {
                this.turn = nextPlayer.getId();
                return;
            }
        }

        this.turn = this.bid.getPlayerId();
//        batakPlayers.stream()
//                .filter(not(BatakPlayer::fold))
//                .min(Comparator.comparingInt(BatakPlayer::getBid))
//                .ifPresentOrElse(
//                        batakPlayer -> this.turn = batakPlayer.getId(),
//                        () -> this.turn = this.getBid().getPlayerId()
//                );

        log.debug("turn has changed from[{}] to[{}]", turnPlayerIndex, ((turnPlayerIndex+1) % 4));
    }

    public BatakPlayer getPlayer(String playerId) {
        return this.batakPlayers.stream()
            .filter((BatakPlayer p) -> p.getId().equals(playerId))
            .findFirst()
            .orElse(null);
    }

    public void setTrump(CardType cardType) {
        BatakBid bid = this.getBid();
        bid.setTrump(cardType);
    }

    public void play(String playerId, Card card) {
        getCurrentTrick().addPlay(playerId, card);
        getPlayer(playerId).remove(card);
    }
    
    public void endTrick() {
        String winner = this.getCurrentTrick().winnerMove(this.bid.getTrump()).getPlayerId();
        getPlayer(winner).addWon(this.tricks.size()-1);
    }

    public void newTrick() {
        this.turn = this.getCurrentTrick().winnerMove(this.bid.getTrump()).getPlayerId();
        this.tricks.add(new BatakTrick());
    }

    public boolean currentTrickEnded() {
        return Objects.requireNonNull(getCurrentTrick()).isDone();
    }
    
    public boolean gameEnded() {
        return (this.tricks.size() == 13 && this.currentTrickEnded());
    }

    public Map<String, Integer> getScores() {
        return batakPlayers.stream()
                .collect(Collectors.toMap(BatakPlayer::getId,
                        batakPlayer -> {
                            if (this.status.equals(BatakStatus.WAITING_TRUMP) || this.status.equals(BatakStatus.BIDDING)) return 0;

                            if (this.bid.getPlayerId().equals(batakPlayer.getId())) {
                                return (this.bid.getValue() > batakPlayer.getScore()) ? this.bid.getValue()*(-1) : batakPlayer.getScore();
                            }
                            else return (batakPlayer.getScore() < 1) ? this.bid.getValue()*(-1) : batakPlayer.getScore();
                        }));
    }

    @Override
    public String toString() {
        return "Batak{" +
                "id='" + id + '\'' +
                ", batakPlayers=" + batakPlayers +
                ", dealer=" + dealer +
                ", turn='" + turn + '\'' +
                ", bid=" + bid +
                ", status=" + status +
                ", tricks=" + tricks +
                '}';
    }

    public void endGame() {
    }
}