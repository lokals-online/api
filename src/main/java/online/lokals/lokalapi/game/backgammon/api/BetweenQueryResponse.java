package online.lokals.lokalapi.game.backgammon.api;

import java.util.Map;

import online.lokals.lokalapi.game.Player;

public record BetweenQueryResponse(Map<Player, Integer> matches)  {
}
