package online.lokals.lokalapi.game.card;

import java.util.List;
import java.util.stream.IntStream;

public enum CardType {
    // ordered!
    CLUBS, DIAMONDS, SPADES, HEARTS;

    // public static CardType fromChar(char typeChar) {
    //     return switch (typeChar) {
    //         case 'CLUBS' -> CLUBS;
    //         case 'DIAMONDS' -> DIAMONDS;
    //         case 'SPADES' -> SPADES;
    //         case 'HEARTS' -> HEARTS;
    //         default -> throw new IllegalArgumentException(String.format("%s is not allowed", typeChar));
    //     };
    // }

    List<Card> packOf() {
        return IntStream.range(2, 15).mapToObj(i -> new Card(i, this)).toList();
    }


}