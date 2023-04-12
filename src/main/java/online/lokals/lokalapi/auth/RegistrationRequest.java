package online.lokals.lokalapi.auth;

import jakarta.annotation.Nonnull;

public record RegistrationRequest(@Nonnull String username, @Nonnull String password) {
}
