package online.lokals.lokalapi.auth;

import jakarta.annotation.Nonnull;

public record LoginRequest(@Nonnull String username, @Nonnull String password) {
}
