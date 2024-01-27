package online.lokals.lokalapi.game.pishti.event;

import online.lokals.lokalapi.game.card.Card;

public record PishtiCardPlayedEvent(String playerId, Card card) {

}