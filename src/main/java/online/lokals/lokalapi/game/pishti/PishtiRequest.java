package online.lokals.lokalapi.game.pishti;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PishtiRequest {

    // TODO: Player{}
    @Nullable
    private String opponent;

    private PishtiSettings settings;

}

record PishtiSettings(int raceTo, @Nullable Integer timeLimit) {}