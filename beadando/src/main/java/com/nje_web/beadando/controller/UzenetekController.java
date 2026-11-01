package com.nje_web.beadando.controller;

import com.nje_web.beadando.repository.UzenetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Üzenetek menü: a Kapcsolat űrlapon küldött üzenetek, a legfrissebb elöl.
// Csak bejelentkezett felhasználó érheti el (WebSecurityConfig: /uzenetek/** -> USER vagy ADMIN)
@Controller
public class UzenetekController {

    private final UzenetRepository uzenetRepository;

    public UzenetekController(UzenetRepository uzenetRepository) {
        this.uzenetRepository = uzenetRepository;
    }

    @GetMapping("/uzenetek")
    public String uzenetekPage(Model model) {
        model.addAttribute("uzenetek", uzenetRepository.findAllByOrderByKuldesIdejeDesc());
        return "uzenetek";
    }
}
