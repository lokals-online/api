package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.exception.ResourceNotFoundException;
import online.lokals.lokalapi.lokal.LokalService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(@Nonnull String username) throws UsernameNotFoundException {

        return userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username:{%s}".formatted(username)));
    }

    public UserDetails loadUserById(@Nonnull String id) {
        try {
            return userService.findById(id);
        }
        catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    public boolean existsByUsername(@Nonnull String username) {
        return userService.existsByUsername(username);
    }
}
