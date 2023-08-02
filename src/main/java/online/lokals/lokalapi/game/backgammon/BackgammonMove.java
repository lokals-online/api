package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackgammonMove {

    @NotNull
    private Integer from;

    @NotNull
    private Integer to;

    private Long movedAt;

    public BackgammonMove(@NotNull Integer from, @NotNull Integer to) {
        this.from = from;
        this.to = to;
    }

    @JsonIgnore
    public int getDice() {
        return from-to;
    }

    @JsonIgnore
    public boolean isFromHitSlot() {return from == Backgammon.HIT_SLOT_INDEX;}

    @JsonIgnore
    public boolean isPicking() {
        return to == Backgammon.PICKING_SLOT_INDEX;
    }
}
