package online.lokals.lokalapi.game.backgammon;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import online.lokals.lokalapi.game.Move;

import java.util.List;

public record BackgammonPlayRequest(String playerId, List<BackgammonMove> moves) {

}