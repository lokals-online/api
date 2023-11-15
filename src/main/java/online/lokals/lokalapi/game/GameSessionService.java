package online.lokals.lokalapi.game;

import jakarta.annotation.Nonnull;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.users.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameSessionService {

    // TODO: use repository
    private final Map<String, GameSession> activeSessions = new HashMap<>(10);

    public Optional<GameSession> findGameSessionByPlayer(@Nonnull String playerId) {
        return activeSessions.values().stream()
                .filter(gameSession -> gameSession.hasPlayer(playerId))
                .findFirst();
    }

    public void setGameSession(@Nonnull GameSession gameSession) {
        activeSessions.put(gameSession.getId(), gameSession);
    }
    public void deleteGameSession(@Nonnull GameSession gameSession) {
        activeSessions.remove(gameSession.getId());
    }

    public boolean quitSession(String sessionId, User user) {
        if (activeSessions.containsKey(sessionId) && activeSessions.get(sessionId).hasPlayer(user.toPlayer().getId())) {
            GameSession gameSession = activeSessions.get(sessionId);
            boolean removed = gameSession.removePlayer(user.toPlayer().getId());

            if (removed) {
                activeSessions.replace(sessionId, gameSession);
                return true;
            }
            else {
                return false;
            }
        }
        else return false;
    }

    public void findGameSessionByTable(String tableId) {

    }
}
