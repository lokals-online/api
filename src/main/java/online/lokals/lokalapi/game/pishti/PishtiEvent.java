package online.lokals.lokalapi.game.pishti;

import lombok.Getter;

public record PishtiEvent(String id, String type, Object payload) {

    public static PishtiEvent cardPlayed(String id, Card card) {
        return new PishtiEvent(id, "CARD_PLAYED", card);
    }

    public static PishtiEvent changeTurn(String pishtiId, String changedTurn) {
        return new PishtiEvent(pishtiId, "CHANGE_TURN", changedTurn);
    }
}
