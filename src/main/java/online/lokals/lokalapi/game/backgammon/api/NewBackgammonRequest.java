package online.lokals.lokalapi.game.backgammon.api;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class NewBackgammonRequest {
    @NotNull
    private final String opponent;
    @NotNull
    private final Map<String, Object> settings;
}
