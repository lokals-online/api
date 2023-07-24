package online.lokals.lokalapi.login;

import jakarta.annotation.Nonnull;

public record LoginRequest(@Nonnull String username, @Nonnull String password) {
}
