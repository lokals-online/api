package online.lokals.lokalapi.game.backgammon;

import lombok.Getter;

import java.util.List;

@Getter
public class BackgammonAction {

    private final String gameId;

    private final String playerId;

    private final BackgammonActionType type;

    private final Object payload;

    private BackgammonAction(String gameId, String playerId, BackgammonActionType type, Object payload) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.type = type;
        this.payload = payload;
    }

    public static BackgammonAction rollDice(String gameId, String playerId, Integer[] rolledDice) {
        return new BackgammonAction(gameId, playerId, BackgammonActionType.ROLL_DICE, rolledDice);
    }

    public static BackgammonAction move(String gameId, String playerId, List<BackgammonMove> moves) {
        return new BackgammonAction(gameId, playerId, BackgammonActionType.MOVE, moves);
    }

}

enum BackgammonActionType {
    ROLL_DICE, MOVE
}