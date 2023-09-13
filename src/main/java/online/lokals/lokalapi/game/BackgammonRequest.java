package online.lokals.lokalapi.game;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.game.backgammon.BackgammonSettings;

@Getter
@Setter
@NoArgsConstructor
public class BackgammonRequest {

    @Nullable
    private String opponent;

    private BackgammonSettings settings;

}
