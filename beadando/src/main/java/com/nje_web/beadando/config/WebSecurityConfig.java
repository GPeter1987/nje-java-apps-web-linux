package com.nje_web.beadando.config;

import com.nje_web.beadando.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// A gyakorlati jegyzet "Autentikáció - Spring-Boot Security" fejezete alapján.
// Eltérés a jegyzethez képest: a szerveren az alkalmazás a /neptunkod/ context path alatt fut,
// ezért a kézi átirányításoknál a request.getContextPath()-t is hozzá kell fűzni az útvonalhoz.
@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Jelszavak titkosítása BCrypt algoritmussal
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // A Spring Security a CustomUserDetailsService-en keresztül tölti be a felhasználókat
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Sikeres bejelentkezés után: admin -> CRUD menü, regisztrált látogató -> Üzenetek menü
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication
                    .getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                response.sendRedirect(request.getContextPath() + "/crud");
            } else {
                response.sendRedirect(request.getContextPath() + "/uzenetek");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                // A REST API-t cURL-ből/Postmanből hívjuk, ott nincs CSRF token
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"))
                // A H2 konzol frame-eket használ
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Statikus fájlok és hibaoldal
                        .requestMatchers("/assets/**", "/images/**", "/error", "/h2-console/**").permitAll()
                        // Látogató (nem bejelentkezett) menüpontjai
                        .requestMatchers("/", "/adatbazis/**", "/kapcsolat/**", "/diagram/**",
                                "/rest/**", "/api/**", "/login", "/register").permitAll()
                        // Regisztrált látogató: Üzenetek menü
                        .requestMatchers("/uzenetek/**").hasAnyRole("USER", "ADMIN")
                        // Admin: CRUD menü
                        .requestMatchers("/crud/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // Saját bejelentkezési oldal (/login), szerep szerinti átirányítással
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                // Kijelentkezés után a főoldalra kerül
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        // Nem bejelentkezett látogató a CRUD menüt nem láthatja: főoldalra kerül
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) ->
                                        response.sendRedirect(request.getContextPath() + "/"),
                                request -> request.getRequestURI()
                                        .substring(request.getContextPath().length())
                                        .startsWith("/crud")
                        )
                        // Minden más védett oldalnál (pl. Üzenetek) a login oldalra kerül
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) ->
                                        response.sendRedirect(request.getContextPath() + "/login"),
                                request -> true
                        )
                        // Bejelentkezett, de nem jogosult felhasználó (pl. USER a CRUD-on): főoldalra kerül
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.sendRedirect(request.getContextPath() + "/")
                        )
                );
        return http.build();
    }
}
