package online.lokals.lokalapi.game;

import jakarta.annotation.Nonnull;

import java.util.List;

public record GamePreviewResponse(@Nonnull String id, @Nonnull String name, @Nonnull String title, List<Player> players) {};