package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nullable;
import online.lokals.lokalapi.game.Player;

import java.util.List;
import java.util.Objects;

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

    public PishtiResponse(Pishti pishti, String currentPlayerName) {
        this.id = pishti.getId();
        this.turn = pishti.getTurn();
        this.stack = pishti.getStack();
        this.remainingCardCount = pishti.getCards().size();

        final PishtiPlayer currentPlayer = pishti.getPlayer(currentPlayerName);
        this.hand = currentPlayer.getHand();
        this.capturedCards = currentPlayer.getCapturedCards();
        this.pishtis = currentPlayer.getPishtis();
        this.score = currentPlayer.getScore();

        PishtiPlayer pishtiOpponent = pishti.getOpponent(currentPlayerName);
        this.opponent = Objects.isNull(pishtiOpponent) ? null : new PishtiOpponentResponse(pishtiOpponent);
    }
}