package online.lokals.lokalapi.game.pishti;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PishtiPlayer {

    private Set<Card> hand = new HashSet<>(4);
    private Set<Card> capturedCards;
    private Set<Card> pishtis;

}