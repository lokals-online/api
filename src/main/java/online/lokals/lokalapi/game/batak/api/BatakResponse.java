package online.lokals.lokalapi.game.batak.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Nullable;
import lombok.Getter;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.batak.*;
import online.lokals.lokalapi.game.card.Card;

@Getter
public class BatakResponse {

    private final String id;
    private final String turn;
    private final List<Card> hand;
    @Nullable
    private final List<Card> availableCards;
    private final BatakBid bid;
    private final List<BatakPlayerResponse> players;
    private final BatakStatus status;
    private final BatakTrick trick;
    private final Map<String, Integer> scores;

    public BatakResponse(Batak batak, Player player) {
        this.id = batak.getId();
        this.turn = batak.getTurn();
        this.hand = batak.getPlayer(player.getId()).getHand();
        this.bid = batak.getBid();
        this.players = batak.getBatakPlayers().stream().map(BatakPlayerResponse::new).toList();
        this.status = batak.getStatus();
        this.trick = batak.getCurrentTrick();
        this.scores = batak.getScores();

        if (turn.equals(player.getId()) && batak.getStatus().equals(BatakStatus.STARTED)) {
            var initial = this.trick.getInitialType();

            if (Objects.isNull(initial)) {
                this.availableCards = this.hand;
            }
            else {
                var trumpPlayed = this.trick.hasTrumpCard(this.bid.getTrump());
                var winnerMove = this.trick.winnerMove(this.bid.getTrump());

                // if initial matches then same type must be played
                var sameWithInitial = this.hand.stream().filter((Card card) -> card.getType().equals(initial)).toList();
                if (!sameWithInitial.isEmpty()) {
                    if (initial.equals(this.bid.getTrump())) {
                        var sameTypeHigher = sameWithInitial.stream().filter((Card card) -> card.getNumber() > winnerMove.getCard().getNumber()).toList();
                        this.availableCards = !sameTypeHigher.isEmpty() ? sameTypeHigher : sameWithInitial;
                    }
                    // if trump card played on current trick then any card with the same type is playable.
                    else if (trumpPlayed) {
                        this.availableCards = sameWithInitial;
                    }
                    else {
                        var sameTypeHigher = sameWithInitial.stream().filter((Card card) -> card.getNumber() > winnerMove.getCard().getNumber()).toList();
                        this.availableCards = !sameTypeHigher.isEmpty() ? sameTypeHigher : sameWithInitial;
                    }
                }
                else if (this.hand.stream().anyMatch((Card card) -> card.getType().equals(this.bid.getTrump()))) {
                    // player has trump cards

                    if (trumpPlayed) {
                        if (this.hand.stream().anyMatch(card -> card.getNumber() > winnerMove.getCard().getNumber())) {
                            this.availableCards = this.hand.stream()
                                .filter((Card card) ->
                                    card.getType().equals(this.bid.getTrump()) &&
                                    card.getNumber() > winnerMove.getCard().getNumber())
                                .toList();
                        }
                        else {
                            this.availableCards = this.hand.stream()
                                    .filter(card -> card.getType().equals(this.bid.getTrump()))
                                    .toList();
                        }
                    }
                    else {
                        this.availableCards = this.hand.stream().filter((Card card) -> card.getType().equals(this.bid.getTrump())).toList();
                    }
                }
                else {
                    this.availableCards = this.hand;
                }
            }
        }
        else {
            this.availableCards = null;
        }
    }

}