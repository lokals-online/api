package online.lokals.lokalapi.game.backgammon;

import jakarta.validation.constraints.NotNull;

// TODO: consider using dice instead of `to` parameter. pros & cons?
public record BackgammonMove(@NotNull Integer from, @NotNull Integer to) {

    public int getDice() {
        return Math.abs((from-to));
    }
}
