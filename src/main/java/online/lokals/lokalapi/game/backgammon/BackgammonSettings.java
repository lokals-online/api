package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.exception.WrongBackgammonSettings;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class BackgammonSettings{
    int raceTo;
    @Nullable
    private Integer timeLimit;

    public BackgammonSettings(Map<String, Object> settings) {

        try {
            this.raceTo = (settings.containsKey("raceTo")) ? (int) settings.get("raceTo") : 2;
            this.timeLimit = 0; // TODO: (settings.containsKey("timeLimit")) ? (int) settings.get("timeLimit") : 0;
        }
        catch (Exception e) {
            throw new WrongBackgammonSettings(settings);
        }


    }
}
