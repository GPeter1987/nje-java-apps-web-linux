package com.nje_web.beadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class RestLeirasController {

    @GetMapping("/rest")
    public String rest(Model model) {
        // A példákban az alkalmazás aktuális címe szerepel (helyben localhost, a szerveren rivendell)
        model.addAttribute("baseUrl", ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
        return "rest";
    }
}
