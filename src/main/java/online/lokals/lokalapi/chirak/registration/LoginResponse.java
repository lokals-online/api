package online.lokals.lokalapi.chirak.registration;

import jakarta.annotation.Nonnull;
import online.lokals.lokalapi.game.Player;

public record LoginResponse(@Nonnull Player player, @Nonnull String token) {
}
