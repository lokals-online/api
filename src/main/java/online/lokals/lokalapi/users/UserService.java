package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;

    public User findById(@Nonnull Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id:{%s}".formatted(id)));
    }

    public User findByUsername(@Nonnull String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    public User create(User user) {
        return userRepository.save(user);
    }
}
