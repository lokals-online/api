package online.lokals.lokalapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserAuthenticationService userAuthenticationService;

    public UserAuthenticationProvider(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(@Nonnull String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 86400000); // 1 day

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withIssuer(username)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }

    public Authentication validateToken(@Nonnull String token) {

        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(secretKey))
                .build();

        DecodedJWT decoded = verifier.verify(token);

        UserDetails userDetails = userAuthenticationService.loadUserByUsername(decoded.getIssuer());

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Authentication validateCredentials(@Nonnull String username, @Nonnull String password) {
        User authenticatedUser = userAuthenticationService.authenticate(username, password);
        return new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
    }


}