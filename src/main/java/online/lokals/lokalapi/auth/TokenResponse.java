package online.lokals.lokalapi.auth;

import jakarta.annotation.Nonnull;

public record TokenResponse(@Nonnull String username, @Nonnull String token) {
}
