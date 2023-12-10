package online.lokals.lokalapi.game.pishti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.card.Card;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.*;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
@Document("pishti")
public class Pishti {

    private static final int TARGET_SCORE = 101;

    @MongoId
    private String id;

    private PishtiPlayer firstPlayer;

    private PishtiPlayer secondPlayer;

    // playerId
    private String turn;

    private List<Card> stack = new ArrayList<>();

    private List<Card> cards = new ArrayList<>();

    private Player winner;

    private PishtiStatus status;

    public Pishti(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = new PishtiPlayer(firstPlayer);
        this.secondPlayer = new PishtiPlayer(secondPlayer);
        this.status = PishtiStatus.INITIALIZED;
    }

    void start() {
        assert Objects.nonNull(firstPlayer) && Objects.nonNull(secondPlayer) && !this.status.equals(PishtiStatus.ENDED);

        // shuffle cards
        this.cards.clear();
        this.shuffleNewCards();

        // set stack
        List<Card> pickedCards = pickFromCards();
        this.stack.addAll(pickedCards);

        // deal 4 cards to each
        this.dealHands();

        if (this.turn == null) {
            // assuming that first player is the dealer at the beginning
            // also note that there is no else condition for turn.
            // because previous last turn indicates the previous dealer.
            // then first turn is the previous dealer for the new series
            this.turn = secondPlayer.getPlayer().getId();
        }

        this.setStatus(PishtiStatus.STARTED);
    }

    public void end() {
        int firstPlayerScore = this.firstPlayer.getScore();
        int secondPlayerScore = this.secondPlayer.getScore();

        if (firstPlayerScore >= TARGET_SCORE && secondPlayerScore >= TARGET_SCORE) {
            if (firstPlayerScore > secondPlayerScore) {
                this.setWinner(this.firstPlayer.getPlayer());
            }
            else if (firstPlayerScore < secondPlayerScore) {
                this.setWinner(this.secondPlayer.getPlayer());
            }
            else {
                throw new IllegalStateException("it is a draw. new series is required to decide winner");
            }
        }
        else if (firstPlayerScore >= TARGET_SCORE) {
            this.setWinner(this.firstPlayer.getPlayer());
        }
        else {
            this.setWinner(this.secondPlayer.getPlayer());
        }

        this.setStatus(PishtiStatus.ENDED);
    }

    void play(@Nonnull String playerId, @Nonnull Card card) {
        PishtiPlayer player = getPlayer(playerId).orElseThrow();

        // remove from hand
        player.played(card);

        // add to deck
        this.stack.add(card);
    }

    public void endRound() {
        log.trace("ending current round. the stack has {} cards. ", this.stack.size());
        // remaining cards are belong to last capturing player
        if (!this.stack.isEmpty()) {
            log.trace("the last one is: {}", this.stack.get(this.stack.size()-1));
            if ((this.stack.size() % 2) == 0) {
                // if stack elements count is even, then the last player is the last capture
                getPlayer(this.turn).get().capture(this.stack);
            }
            else {
                getOpponent(this.turn).get().capture(this.stack);
            }
            this.stack.clear();
        }
        this.firstPlayer.endRound();
        this.secondPlayer.endRound();

        assert this.stack.isEmpty() &&
                this.cards.isEmpty() &&
                this.firstPlayer.getHand().isEmpty() &&
                this.secondPlayer.getHand().isEmpty();
    }

    public boolean checkGameEnded() {
        if (this.firstPlayer.getScore() == this.secondPlayer.getScore()) return false;

        return (this.firstPlayer.getScore() >= TARGET_SCORE || this.secondPlayer.getScore() >= TARGET_SCORE);
    }

    public boolean checkPishti() {
        boolean isPishti = this.stack.size() == 2 && this.stack.get(0).getNumber() == this.stack.get(1).getNumber();

        if (isPishti) {
            // player add pishti
            getPlayer(turn).get().pishti(this.stack.get(0), this.stack.get(1));

            // clear stack
            this.stack.clear();

            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkCapture() {
        if (this.stack.size() >= 2) {
            int size = this.stack.size();
            Card lastCard = this.stack.get(size - 1);

            boolean isCapture = (lastCard.getNumber() == 11) || this.stack.get(size - 2).getNumber() == lastCard.getNumber();

            if (isCapture) {
                // player add capture
                getPlayer(turn).get().capture(this.stack);
                // clear stack
                this.stack.clear();
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public void dealHands() {
        if (this.firstPlayer.getHand().isEmpty() && this.secondPlayer.getHand().isEmpty()) {
            log.trace("dealing hands from {}", this.cards.size());

            List<Card> forFirstPlayer = pickFromCards();
            log.trace("picked cards: {} for firstPlayer[{}]", forFirstPlayer, this.firstPlayer.getPlayerUsername());
            this.firstPlayer.setHand(forFirstPlayer);

            List<Card> forSecondPlayer = pickFromCards();
            log.trace("picked cards: {} for secondPlayer[{}]", forSecondPlayer, this.secondPlayer.getPlayerUsername());
            this.secondPlayer.setHand(forSecondPlayer);
        }
    }

    public String changeTurn() {
        
        PishtiPlayer opponent = getOpponent(this.turn).orElseThrow(IllegalStateException::new);

        log.trace("changing turn from[{}] to[{}]", turn, opponent);

        this.turn = opponent.getPlayer().getId();
        return this.turn;
    }

    @JsonIgnore
    @Nonnull
    public List<PishtiPlayer> getPlayers() {
        if (secondPlayer != null) {
            return List.of(firstPlayer, secondPlayer);
        }
        else {
            return List.of(firstPlayer);
        }
    }

    public Optional<PishtiPlayer> getPlayer(@Nonnull String playerId) {
        return getPlayers().stream()
                .filter(player -> player.getPlayer().getId().equals(playerId))
                .findFirst();
    }

    @Nullable
    public Optional<PishtiPlayer> getOpponent(@Nonnull String playerId) {
        return getPlayers().stream()
                .filter(player -> !player.getPlayer().getId().equals(playerId))
                .findFirst();
    }

    // TODO: check if this.cards has enough cards!
    private List<Card> pickFromCards() {
        List<Card> pickedCards = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            pickedCards.add(this.cards.remove(0));
        }
        return pickedCards;
    }

    public boolean checkRoundEnded() {
        return (this.cards.isEmpty()) && (this.firstPlayer.getHand().isEmpty()) && this.secondPlayer.getHand().isEmpty();
    }

    public void startNewSeries() {
        this.start();
    }

    private void shuffleNewCards() {
        this.cards.clear();
        this.cards = new ArrayList<>(Card.ofCheatingPack());
//        Collections.shuffle(this.cards);

        log.trace("new series cards({}) are shuffled...", this.cards.size());
    }

}
