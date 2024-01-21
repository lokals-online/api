package online.lokals.lokalapi.security;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import online.lokals.lokalapi.users.User;

@Component
public class LokalTokenManager {

    @Value("${jwt.secret:parolaisaret}")
    private String jwtSecret;

    @Value("${jwt.validityInDays:365}")
    private Integer validityInDays;
    
    public String generate(UserDetails userDetails) {

        final long now = System.currentTimeMillis();
        long expirationInMs = validityInDays * Duration.ofDays(1).toMillis();

        User user = (User) userDetails;

        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setId(user.getId())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }
    public Boolean validate(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        boolean isTokenExpired = claims.getExpiration().before(new Date());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired);
    }
    public String getUsernameFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getIdFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getId();
    }

}
