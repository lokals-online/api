package online.lokals.lokalapi.login;

import jakarta.annotation.Nonnull;

public record LoginResponse(@Nonnull String username, @Nonnull String token) {
}
