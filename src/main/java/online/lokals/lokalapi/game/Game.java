package online.lokals.lokalapi.game;

import jakarta.annotation.Nonnull;

// TODO: make abstract?
public interface Game {

    @Nonnull String getId();

    @Nonnull String getName();

    @Nonnull Player[] getPlayers();

}
