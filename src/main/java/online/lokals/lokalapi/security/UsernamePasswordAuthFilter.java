package online.lokals.lokalapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import online.lokals.lokalapi.auth.LoginRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UsernamePasswordAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final UserAuthenticationProvider userAuthenticationProvider;

    public UsernamePasswordAuthFilter(UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        if ("/login".equals(httpServletRequest.getServletPath()) && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            LoginRequest loginRequest = MAPPER.readValue(httpServletRequest.getInputStream(), LoginRequest.class);

            try {
                SecurityContextHolder.getContext().setAuthentication(
                        userAuthenticationProvider.validateCredentials(loginRequest.username(), loginRequest.password())
                );
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                throw e;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


}