package online.lokals.lokalapi.config;

import static online.lokals.lokalapi.config.LokalConfiguration.LOKAL_USER_ID_HEADER;
import static online.lokals.lokalapi.config.LokalConfiguration.LOKAL_USER_TOKEN_HEADER;
import static online.lokals.lokalapi.users.User.OYUNCU_ROLE;
import static online.lokals.lokalapi.users.User.USER_ROLE;

import java.util.List;

import online.lokals.lokalapi.security.LokalTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;

@EnableWebSecurity
@Configuration
@AllArgsConstructor
public class WebConfig {

    private final LokalTokenFilter lokalTokenFilter;

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeHttpRequests((authorize) ->
                    authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/lokal-ws/**").permitAll()
                        .requestMatchers("/chirak/hello", "/chirak/login").permitAll()
                        .requestMatchers("/chirak/register").hasAuthority(OYUNCU_ROLE.getAuthority())
                        .requestMatchers("/chirak/table/**").hasAnyAuthority(OYUNCU_ROLE.getAuthority(), USER_ROLE.getAuthority())
                        // .requestMatchers("/pishti/**").hasAnyAuthority(OYUNCU_ROLE.getAuthority(), USER_ROLE.getAuthority())
                        .anyRequest()
                        .authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(lokalTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:[*]",
                "https://*.lokals.online:[*]",
                "http://*.lokals.online:[*]"
//                "http://192.168.48.192:[*]"
        ));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setExposedHeaders(List.of(LOKAL_USER_ID_HEADER, LOKAL_USER_TOKEN_HEADER));
        configuration.setAllowedHeaders(List.of("Authorization", "content-type", LOKAL_USER_ID_HEADER, LOKAL_USER_TOKEN_HEADER));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}