package online.lokals.lokalapi.chirak.registration;

import jakarta.annotation.Nonnull;

public record LoginRequest(@Nonnull String username, @Nonnull String password) {
}
