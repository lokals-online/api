package online.lokals.lokalapi.login;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    @Value("${jwt.validityInDays:1}")
    private Integer validityInDays;

//    private final JwtEncoder encoder;
//
//    @PostMapping(value = "/login")
//    public @ResponseBody LoginResponse login(Authentication authentication) {
//        Instant now = Instant.now();
//        long expiry = validityInDays * Duration.ofDays(1).toMillis();
//        // @formatter:off
//        String scope = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(" "));
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(now)
//                .expiresAt(now.plusMillis(expiry))
//                .subject(authentication.getName())
//                .claim("scope", scope)
//                .build();
//        // @formatter:on
//        String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//
//        return new LoginResponse(authentication.getName(), token);
//    }

}

