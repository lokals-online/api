package online.lokals.lokalapi.game.batak;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.card.Card;
import online.lokals.lokalapi.game.card.CardType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BatakTrick {

    private List<BatakMove> moves;

    public void addPlay(String playerId, Card card) {
        this.moves.add(new BatakMove(playerId, card));
    }

    public BatakTrick() {
        this.moves = new ArrayList<>(4);
    }

    public boolean isDone() {
        return this.moves.size() == 4;
    }

    public CardType getInitialType() {
        if (this.moves.isEmpty()) return null;
        else return this.moves.get(0).getCard().getType();
    }

    public BatakMove winnerMove(@Nonnull CardType trump) {
        BatakMove winnerMove = moves.get(0);

        for (BatakMove move : moves) {
            if (move.getCard().getType().equals(winnerMove.getCard().getType())) {
                if (move.getCard().getNumber() > winnerMove.getCard().getNumber()) {
                    winnerMove = move;
                }
            }
            else if (!winnerMove.getCard().getType().equals(trump) && move.getCard().getType().equals(trump)) {
                winnerMove = move;
            }
        }

        return winnerMove;
    }

    public boolean hasTrumpCard(@Nonnull CardType cardType) {
        return moves.stream().anyMatch((BatakMove m) -> m.getCard().getType().equals(cardType));
    }

}
