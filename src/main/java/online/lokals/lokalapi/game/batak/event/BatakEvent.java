package online.lokals.lokalapi.game.batak.event;

public record BatakEvent(String id, String type, Object payload) {

    // public static PishtiEvent cardPlayed(String id, Card card) {
    //     return new PishtiEvent(id, "CARD_PLAYED", card);
    // }

    public static BatakEvent changeTurn(String batakId, String changedTurn) {
        return new BatakEvent(batakId, "CHANGE_TURN", changedTurn);
    }
}