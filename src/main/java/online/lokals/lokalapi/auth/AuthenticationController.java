package online.lokals.lokalapi.auth;

import lombok.RequiredArgsConstructor;
import online.lokals.lokalapi.security.UserAuthenticationProvider;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserAuthenticationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserAuthenticationService userAuthenticationService;

    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/register")
    public void registerUser(@RequestBody RegistrationRequest request) {
        userAuthenticationService.register(request.username(), request.password());
    }

    @PostMapping("/login")
    public @ResponseBody TokenResponse login(@AuthenticationPrincipal User user) {
        String token = userAuthenticationProvider.createToken(user.getUsername());

        return new TokenResponse(user.getUsername(), token);
    }

}

