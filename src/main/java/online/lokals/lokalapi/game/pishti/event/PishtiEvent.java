package online.lokals.lokalapi.game.pishti.event;

import online.lokals.lokalapi.game.card.Card;

public record PishtiEvent(String id, String type, Object payload) {
    public static PishtiEvent madePishti(String pishtiId, String playerId) {
        return new PishtiEvent(pishtiId, "MADE_PISHTI", playerId);
    }
}

