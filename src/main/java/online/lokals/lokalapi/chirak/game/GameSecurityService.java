package online.lokals.lokalapi.chirak.game;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.game.GameSession;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.users.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameSecurityService {

//    private final GameSessionService gameSessionService;

//    public boolean isOwner(String sessionId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Player player = ((User) authentication.getPrincipal()).toPlayer();
//
//        if (!authentication.isAuthenticated()) return false;
//
//        Optional<GameSession> gameSessionByPlayer = gameSessionService.findGameSessionByPlayer(player.getId());
//        return (gameSessionByPlayer.isPresent() && gameSessionByPlayer.get().getHome().getId().equals(player.getId()));
//    }

}
