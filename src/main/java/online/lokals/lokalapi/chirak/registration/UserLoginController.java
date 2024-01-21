package online.lokals.lokalapi.chirak.registration;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.security.LokalTokenManager;
import online.lokals.lokalapi.users.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserLoginController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final LokalTokenManager lokalTokenManager;

    @PostMapping("/chirak/hello")
    public LoginResponse anonymousLogin() {

        User anonymous = User.oyuncu();
        String token = lokalTokenManager.generate(anonymous);

        return new LoginResponse(anonymous.toPlayer(), token);
    }

    @PostMapping("/chirak/login")
    public LoginResponse login(@RequestBody LoginRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String token = lokalTokenManager.generate(userDetails);

        return new LoginResponse(((User) userDetails).toPlayer(), token);
    }

}

