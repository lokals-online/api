package online.lokals.lokalapi.chirak.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.backgammon.BackgammonSession;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChirakGameSessionResponse {
    private GameSession backgammonSession;
}
