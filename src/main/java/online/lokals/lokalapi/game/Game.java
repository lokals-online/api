package online.lokals.lokalapi.game;

import jakarta.annotation.Nonnull;

import java.util.List;

// TODO: make abstract?
public interface Game {

    @Nonnull String getId();

    @Nonnull String getName();

    @Nonnull
    List<? extends Player> getPlayers();

}
