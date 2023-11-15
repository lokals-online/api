package online.lokals.lokalapi.game.pishti.api;

import jakarta.annotation.Nonnull;
import online.lokals.lokalapi.game.pishti.Card;

public record PishtiPlayRequest(@Nonnull Card card) {}
