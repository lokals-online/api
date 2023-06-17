package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.game.Player;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(@Nonnull String id) throws ResourceNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id:{%s}".formatted(id)));
    }

    public User findByUsername(@Nonnull String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());

            throw e;
        }
    }

    public boolean existsByUsername(@Nonnull String username) {
        return userRepository.existsByUsername(username);
    }

    public Player findPlayerById(String playerId) {
//        return findById(playerId).toPlayer();
        return new Player("edfafa", "username");
    }
}
