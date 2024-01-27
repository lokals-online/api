package online.lokals.lokalapi.game.pishti;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.game.card.Card;
import online.lokals.lokalapi.game.pishti.api.PishtiOpponentResponse;

public class PishtiResponse {

    public String id;
    public String turn;
    public int remainingCardCount;
    public List<Card> hand;
    public List<Card> capturedCards;
    public List<Card> pishtis;

    public int score;

    @Nullable
    public PishtiOpponentResponse opponent;
    public List<Card> stack;

    public PishtiResponse(Pishti pishti, Player player) {
        this.id = pishti.getId();
        this.turn = pishti.getTurn();
        this.stack = pishti.getPile();
        this.remainingCardCount = pishti.getCards().size();

        final Optional<PishtiPlayer> currentPlayerOptional = pishti.getPlayer(player.getId());
        if (currentPlayerOptional.isPresent()) {
            var currentPlayer = currentPlayerOptional.get();
            this.hand = currentPlayer.getHand();
            this.capturedCards = currentPlayer.getCapturedCards();
            this.pishtis = currentPlayer.getPishtis();
            this.score = currentPlayer.getScore();
        }

        Optional<PishtiPlayer> pishtiOpponentOptional = pishti.getOpponent(player.getId());

        assert Objects.requireNonNull(pishtiOpponentOptional).isPresent();
        pishtiOpponentOptional.ifPresent(pishtiPlayer -> this.opponent = new PishtiOpponentResponse(pishtiPlayer));
    }
}