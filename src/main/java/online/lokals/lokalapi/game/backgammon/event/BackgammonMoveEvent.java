package online.lokals.lokalapi.game.backgammon.event;

import online.lokals.lokalapi.game.backgammon.BackgammonMove;

public record BackgammonMoveEvent(String playerId, BackgammonMove move) {
}
