package online.lokals.lokalapi.game;

public interface GameSession {

    String getId();
    String getKey();
    // Player getHome();
    // int getHomeScore();
    // Player getAway();
    // int getAwayScore();

    // boolean removePlayer(@Nonnull String playerId);

    // default boolean hasPlayer(@Nonnull String playerId) {
    //     return (getHome() != null && playerId.equals(getHome().getId()) ||
    //             (getAway() != null && playerId.equals(getAway().getId())));
    // }
}
