package online.lokals.lokalapi.game.backgammon;

import lombok.Getter;

import java.util.List;

@Getter
public class BackgammonPlayerAction {

    private final String gameId;

    private final String playerId;

    private final BackgammonActionType type;

    private final Object payload;

    private BackgammonPlayerAction(String gameId, String playerId, BackgammonActionType type, Object payload) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.type = type;
        this.payload = payload;
    }

    public static BackgammonPlayerAction firstDice(String gameId, String playerId, int firstDice) {
        return new BackgammonPlayerAction(gameId, playerId, BackgammonActionType.FIRST_DICE, firstDice);
    }

    public static BackgammonPlayerAction rollDice(String gameId, String playerId, Integer[] rolledDice) {
        return new BackgammonPlayerAction(gameId, playerId, BackgammonActionType.ROLL_DICE, rolledDice);
    }

    public static BackgammonPlayerAction move(String gameId, String playerId, List<BackgammonMove> moves) {
        return new BackgammonPlayerAction(gameId, playerId, BackgammonActionType.MOVE, moves);
    }
}

enum BackgammonActionType {
    FIRST_DICE, ROLL_DICE, MOVE
}