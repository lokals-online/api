package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.common.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(@Nonnull String username) throws UsernameNotFoundException {
        try {
            return userService.findByUsername(username);
        }
        catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    // TODO: validate
    // TODO: assert request.password() called only once to encrypt
    public User register(@Nonnull String username, @Nonnull String password) {
        assert validateRegistration();

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(username, encodedPassword);
        return userService.create(user);
    }

    public User authenticate(@Nonnull String username, @Nonnull String password) {
        User user = (User) loadUserByUsername(username);

        assert passwordEncoder.matches(password, user.getPassword()) : "Password doesn't match";

        return user;
    }

    private boolean validateRegistration() {
        return true;
    }

}
