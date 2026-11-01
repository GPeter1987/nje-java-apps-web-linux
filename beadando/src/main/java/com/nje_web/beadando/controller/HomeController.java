package com.nje_web.beadando.controller;

import com.nje_web.beadando.model.Labdarugo;
import com.nje_web.beadando.service.FooldalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final FooldalService fooldalService;

    public HomeController(FooldalService fooldalService) {
        this.fooldalService = fooldalService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Labdarugo> labdarugok = fooldalService.osszesLabdarugo();
        model.addAttribute("klubok", fooldalService.klubOsszesitesek(labdarugok));
        model.addAttribute("legertekesebbek", fooldalService.legertekesebbek(labdarugok, 5));
        model.addAttribute("jatekosokSzama", labdarugok.size());
        model.addAttribute("osszertek", fooldalService.osszertek(labdarugok));
        model.addAttribute("kulfoldiArany", fooldalService.kulfoldiArany(labdarugok));
        model.addAttribute("legnepesebbPoszt", fooldalService.legnepesebbPoszt(labdarugok));
        return "index";
    }
}
