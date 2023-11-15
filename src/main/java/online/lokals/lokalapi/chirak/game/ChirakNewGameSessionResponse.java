package online.lokals.lokalapi.chirak.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ChirakNewGameSessionResponse {
    private String id;
    private String key;
//    private GameSession gameSession;
}