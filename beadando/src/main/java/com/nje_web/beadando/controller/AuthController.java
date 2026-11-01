package com.nje_web.beadando.controller;

import com.nje_web.beadando.dto.RegisztracioForm;
import com.nje_web.beadando.model.User;
import com.nje_web.beadando.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Bejelentkezés és regisztráció (a kijelentkezést a Spring Security /logout végpontja kezeli)
@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisztracioForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisztracioForm form, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("username")
                && userRepository.findByUsername(form.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "taken", "Ez a felhasználónév már foglalt.");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Regisztrációval mindig USER szerepkörű felhasználó jön létre
        User user = new User(form.getUsername(), passwordEncoder.encode(form.getPassword()), "USER");
        userRepository.save(user);

        // Sikeres regisztráció után a login oldalra irányítjuk
        return "redirect:/login?registered";
    }
}
