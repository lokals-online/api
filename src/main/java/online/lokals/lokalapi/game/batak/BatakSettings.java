package online.lokals.lokalapi.game.batak;

import java.util.Map;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.lokals.lokalapi.exception.WrongBackgammonSettings;

@Getter
@Setter
@NoArgsConstructor
public class BatakSettings {

    private int raceTo;
    
    @Nullable
    private Integer timeLimit;

    public BatakSettings(Map<String, Object> settings) {
        try {
            this.raceTo = (settings.containsKey("raceTo")) ? (int) settings.get("raceTo") : 11;
            this.timeLimit = 0;
        }
        catch (Exception e) {
            throw new WrongBackgammonSettings(settings);
        }
    }

}
