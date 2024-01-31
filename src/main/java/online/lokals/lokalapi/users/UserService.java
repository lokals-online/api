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
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

//    @Getter
//    private final Set<Long> registrationPool;

    public UserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
//        this.registrationPool = new HashSet<>();
    }

    public User findById(@Nonnull String id) throws ResourceNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id:{%s}".formatted(id)));
    }

    public Optional<User> findByIdOptional(@Nonnull String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(@Nonnull String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User register(@Nonnull String id, @Nonnull String username, @Nonnull String encodedPassword) {

        User user = new User(username, encodedPassword);
        user.setId(id);

        create(user);

        assert user.getId() != null;

        return user;
    }

    public User register(@Nonnull String username, @Nonnull String encodedPassword) {

        User user = new User(username, encodedPassword);

        create(user);

        assert user.getId() != null;

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

    public void update(User user) {
        userRepository.save(user);
    }

    public boolean existsByUsername(@Nonnull String username) {
        return userRepository.existsByUsername(username);
    }

    // move to player service/repository!
    /**
     *
     * @param currentUser (@AuthenticatedPrincipal User currentUser)
     * @return
     */
    public User getOrCreatePlayer(User currentUser) {
        Optional<User> userOptional = findByIdOptional(currentUser.getId());

        // if user is not persisted.
        // !!! this behaviour is to provide game for anonymous users. this should be changed !!!!!
        return userOptional.orElseGet(() -> register(currentUser.getId(), currentUser.getUsername(), User.ANONYMOUS_PASSWORD));
    }
}
