package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.game.Player;
import online.lokals.lokalapi.lokal.LokalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final LokalService lokalService;

//    @Getter
//    private final Set<Long> registrationPool;

    public UserService(@Autowired UserRepository userRepository, @Autowired LokalService lokalService) {
        this.userRepository = userRepository;
        this.lokalService = lokalService;
//        this.registrationPool = new HashSet<>();
    }

    public User findById(@Nonnull String id) throws ResourceNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id:{%s}".formatted(id)));
    }

    public User findByUsername(@Nonnull String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    public User register(@Nonnull String username, @Nonnull String encodedPassword) {

        User user = new User(username, encodedPassword);

        create(user);

        assert user.getId() != null;

//        lokalService.createLokal(username, user);

        return user;
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

}
