package com.nje_web.beadando.config;

import com.nje_web.beadando.model.User;
import com.nje_web.beadando.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Induláskor létrehozza az ADMIN felhasználót, ha még nincs ilyen nevű felhasználó.
// A jelszó nincs a (nyilvános) GitHub repóban: helyben az application-local.properties,
// a szerverre készülő WAR-ban a git által figyelmen kívül hagyott application-secret.properties adja meg.
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       @Value("${app.admin.username:admin}") String adminUsername,
                       @Value("${app.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }
        if (adminPassword.isBlank()) {
            log.warn("Nincs megadva app.admin.password, ezért nem jött létre admin felhasználó.");
            return;
        }
        userRepository.save(new User(adminUsername, passwordEncoder.encode(adminPassword), "ADMIN"));
        log.info("Admin felhasználó létrehozva: {}", adminUsername);
    }
}
