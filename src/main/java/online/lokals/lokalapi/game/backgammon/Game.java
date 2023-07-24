package online.lokals.lokalapi.game.backgammon;

import jakarta.annotation.Nonnull;
import online.lokals.lokalapi.game.Player;

import java.util.List;

public interface Game {

    @Nonnull
    String getId();

    @Nonnull String getName();

    @Nonnull
    List<? extends Player> getPlayers();

}
