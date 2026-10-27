package com.nje_web.beadando.controller;

import com.nje_web.beadando.dto.KapcsolatForm;
import com.nje_web.beadando.model.Uzenet;
import com.nje_web.beadando.repository.UzenetRepository;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

// Kapcsolat menü: űrlap szerver oldali validációval, az üzenet adatbázisba mentése
@Controller
public class KapcsolatController {

    private final UzenetRepository uzenetRepository;

    public KapcsolatController(UzenetRepository uzenetRepository) {
        this.uzenetRepository = uzenetRepository;
    }

    // A mezők elejéről/végéről levágjuk a szóközöket, az üres mező null lesz:
    // így üres mezőnél csak a "kötelező" hibaüzenet jelenik meg, a hossz-ellenőrzés nem
    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/kapcsolat")
    public String kapcsolatPage(Model model) {
        model.addAttribute("form", new KapcsolatForm());
        return "kapcsolat";
    }

    @PostMapping("/kapcsolat")
    public String kuldes(@Valid @ModelAttribute("form") KapcsolatForm form, BindingResult bindingResult,
                         Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "kapcsolat";
        }

        // Bejelentkezett felhasználónál a felhasználónevet is eltároljuk
        String felhasznalo = principal != null ? principal.getName() : null;
        Uzenet uzenet = new Uzenet(form.getNev(), form.getEmail(), form.getTargy(), form.getSzoveg(), felhasznalo);
        uzenetRepository.save(uzenet);

        // Post/Redirect/Get: frissítéskor ne küldődjön el újra az űrlap
        redirectAttributes.addFlashAttribute("siker", true);
        return "redirect:/kapcsolat";
    }
}
