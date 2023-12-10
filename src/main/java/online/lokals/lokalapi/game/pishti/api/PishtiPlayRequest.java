package online.lokals.lokalapi.game.pishti.api;

import jakarta.annotation.Nonnull;
import online.lokals.lokalapi.game.card.Card;

public record PishtiPlayRequest(@Nonnull Card card) {}
