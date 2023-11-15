package online.lokals.lokalapi.chirak;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.game.*;
import online.lokals.lokalapi.game.backgammon.BackgammonSessionService;
import online.lokals.lokalapi.security.LokalTokenManager;
import online.lokals.lokalapi.table.Table;
import online.lokals.lokalapi.table.TableService;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Chirak {

    private final TableService tableService;
    private final UserService userService;
    private final BackgammonSessionService backgammonSessionService;
    private final LokalTokenManager lokalTokenManager;

    public String getToken(User user) {
        return lokalTokenManager.generate(user);
    }

}