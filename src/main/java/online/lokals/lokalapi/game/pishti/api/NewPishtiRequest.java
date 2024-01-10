package online.lokals.lokalapi.game.pishti.api;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class NewPishtiRequest {
    @NotNull
    private final String opponent;
    @NotNull
    private final Map<String, Object> settings;
}
