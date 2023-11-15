package online.lokals.lokalapi.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.lokals.lokalapi.users.User;
import online.lokals.lokalapi.users.UserAccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static online.lokals.lokalapi.config.LokalConfiguration.*;
import static online.lokals.lokalapi.users.User.OYUNCU_USERNAME;

@Slf4j
@Component
@AllArgsConstructor
public class LokalTokenFilter extends OncePerRequestFilter {

    private final UserAccountService userDetailsService;

    private final LokalTokenManager lokalTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().equals("/chirak/hello") || request.getServletPath().startsWith("/lokal-ws")) {
            log.trace("hello request is allowed");
            filterChain.doFilter(request, response);
        }
        else {
            String lokalUserId = request.getHeader(LOKAL_USER_ID_HEADER);
            String lokalUserTokenWithBearer = request.getHeader("Authorization");
            String lokalUserToken = null;

            if (lokalUserTokenWithBearer != null) {
                lokalUserToken = lokalUserTokenWithBearer.substring("Bearer ".length());
            }

            if (lokalUserToken != null) {
                log.trace(lokalUserToken, lokalUserId);

                handleAuthenticated(request, lokalUserId, lokalUserToken);

                filterChain.doFilter(request, response);
            }
            else {
                log.warn("token not found");
            }
        }
    }

    private void handleAuthenticated(HttpServletRequest request, String lokalUserId, String lokalUserToken) {
        try {
            String usernameFromToken = lokalTokenManager.getUsernameFromToken(lokalUserToken);
            String userIdFromToken = lokalTokenManager.getIdFromToken(lokalUserToken);

            if (OYUNCU_USERNAME.equals(usernameFromToken) && userIdFromToken.equals(lokalUserId) && User.isOyuncu(userIdFromToken)) {
                User oyuncu = User.getOyuncu(lokalUserId);
                var authenticationToken = new UsernamePasswordAuthenticationToken(oyuncu, null, oyuncu.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            else {
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);

                if (lokalTokenManager.validate(lokalUserToken, userDetails)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

        } catch (UsernameNotFoundException e) {
            log.warn(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Unable to get JWT Token");
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token has expired");
        }
    }

}
