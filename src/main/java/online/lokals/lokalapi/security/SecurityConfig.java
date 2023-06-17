package online.lokals.lokalapi.security;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .httpBasic()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(authenticationEntryPoint())
//                .and()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
//                        .anyRequest().authenticated())
//                ;
//
//        return http.build();
//    }
//
//    // TODO[auth]: https://trello.com/c/J3VTtXh2/1-user-authentication
//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        return (request, response, authException) -> response.sendError(response.getStatus(), authException.getMessage());
//    }

}