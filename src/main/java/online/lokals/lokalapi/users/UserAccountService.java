package online.lokals.lokalapi.users;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.lokal.LokalService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//public class UserAccountService implements UserDetailsService {
public class UserAccountService {

    private final UserService userService;
    private final LokalService lokalService;
//    private final PasswordEncoder passwordEncoder;

//    @Override
//    public UserDetails loadUserByUsername(@Nonnull String username) throws UsernameNotFoundException {
//        try {
//            return userService.findByUsername(username);
//        }
//        catch (ResourceNotFoundException e) {
//            throw new UsernameNotFoundException(e.getMessage(), e);
//        }
//    }

    public User register(@Nonnull String username, @Nonnull String password) {

        final String encodedPassword = "secretpass";// passwordEncoder.encode(password);

        User user = new User(username, encodedPassword);

        userService.create(user);

        assert user.getId() != null;

        lokalService.createLokal(username, user);

        return user;
    }

    public boolean existsByUsername(@Nonnull String username) {
        return userService.existsByUsername(username);
    }
}
