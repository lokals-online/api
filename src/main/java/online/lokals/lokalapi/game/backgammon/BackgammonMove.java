package online.lokals.lokalapi.game.backgammon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;

// TODO: consider using dice instead of `to` parameter. pros & cons?
public record BackgammonMove(@NotNull Integer from, @NotNull Integer to) {

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
