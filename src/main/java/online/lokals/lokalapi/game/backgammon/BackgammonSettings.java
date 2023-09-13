package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nullable;

public record BackgammonSettings(int raceTo, @Nullable Integer timeLimit) {}
