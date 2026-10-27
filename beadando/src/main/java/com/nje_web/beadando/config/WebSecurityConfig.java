package com.nje_web.beadando.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Az útvonalak jogosultságai már a végleges menüszerkezet szerint vannak megadva,
// így a menüpontok párhuzamosan fejleszthetők.
// TODO (A - Autentikáció): User entitás, CustomUserDetailsService, PasswordEncoder,
//      saját /login és /register oldal, szerep szerinti átirányítás - a gyakorlati jegyzet alapján.
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());
        return http.build();
    }
}
