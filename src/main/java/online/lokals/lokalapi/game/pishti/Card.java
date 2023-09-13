package online.lokals.lokalapi.game.pishti;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static online.lokals.lokalapi.game.pishti.CardType.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private int number;
    private CardType type;

    public static Card valueOf(String fromString) {
        if (fromString.length() > 2) throw new IllegalArgumentException("%s is not allowed");

        char numberChar = fromString.toCharArray()[0];
        char typeChar = fromString.toCharArray()[1];
        try {
            CardType type = CardType.fromChar(typeChar);

            return new Card(numberChar, type);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("%s is invalid. only [1-13] are allowed.", numberChar));
        }
    }

    public static List<Card> ofStandardPack() {
        return Arrays.stream(values())
                .map(CardType::packOf)
                .flatMap(Collection::stream)
                .limit(20)
                .toList();
    }

    public static List<Card> ofCheatingPack() {
        return Arrays.asList(
                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 0
                new Card(1, DIAMONDS), new Card(2, DIAMONDS), new Card(10, DIAMONDS), new Card(11, DIAMONDS), // 1
                new Card(1, CLUBS), new Card(2, CLUBS), new Card(10, CLUBS), new Card(11, CLUBS), // 1
                new Card(1, SPADES), new Card(2, SPADES), new Card(10, SPADES), new Card(11, SPADES), // 2
                new Card(1, HEARTS), new Card(2, HEARTS), new Card(10, HEARTS), new Card(11, HEARTS), // 2
                new Card(3, SPADES), new Card(4, SPADES), new Card(10, SPADES), new Card(11, SPADES), // 3
                new Card(1, HEARTS), new Card(2, HEARTS), new Card(10, HEARTS), new Card(11, HEARTS) // 3
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 3
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 3
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 4
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 4
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 5
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 5
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 6
//                new Card(6, CLUBS), new Card(7, CLUBS), new Card(8, DIAMONDS), new Card(9, DIAMONDS), // 6
        );
    }

    public int pishtiValue() {
        if (this.getNumber() == 1 || this.getNumber() == 11) {
            return 1;
        }
        else if (this.getNumber() == 2 && this.getType().equals(CardType.CLUBS)) {
            return 2;
        }
        else if (this.getNumber() == 10 && this.getType().equals(CardType.DIAMONDS)) {
            return 3;
        }
        else return 0;
    }

    @Override
    public String toString() {
        return "Card{" + number + "[" + type.toString().toUpperCase() + "]}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (number != card.number) return false;
        return type == card.type;
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + type.hashCode();
        return result;
    }
}

enum CardType {
    CLUBS, HEARTS, DIAMONDS, SPADES;

    public static CardType fromChar(char typeChar) {
        return switch (typeChar) {
            case 'c' -> CLUBS;
            case 'h' -> HEARTS;
            case 'd' -> DIAMONDS;
            case 's' -> SPADES;
            default -> throw new IllegalArgumentException(String.format("%s is not allowed", typeChar));
        };
    }

    List<Card> packOf() {
        return IntStream.range(1, 14).mapToObj(i -> new Card(i, this)).toList();
    }
}
