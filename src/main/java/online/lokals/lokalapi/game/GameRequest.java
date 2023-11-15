package online.lokals.lokalapi.game;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.backgammon.BackgammonSettings;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GameRequest {

    @NotNull
    private String gameKey;

    @NotNull
    private String opponent;

    private Map<String, Object> settings;

}
