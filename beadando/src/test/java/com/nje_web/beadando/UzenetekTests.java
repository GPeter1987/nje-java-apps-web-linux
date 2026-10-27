package com.nje_web.beadando;

import com.nje_web.beadando.model.Uzenet;
import com.nje_web.beadando.repository.UzenetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UzenetekTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UzenetRepository uzenetRepository;

    @BeforeEach
    void adatok() {
        uzenetRepository.deleteAll();
        Uzenet regi = new Uzenet("Régi Feladó", "regi@example.com", "Régi tárgy", "Régebbi üzenet", null);
        regi.setKuldesIdeje(LocalDateTime.of(2026, 10, 1, 9, 5));
        Uzenet uj = new Uzenet("Új Feladó", "uj@example.com", "Új tárgy", "Frissebb üzenet", "tesztuser");
        uj.setKuldesIdeje(LocalDateTime.of(2026, 10, 20, 18, 30));
        uzenetRepository.saveAll(List.of(regi, uj));
    }

    @Test
    void latogatoBejelentkezesreIranyitva() throws Exception {
        mockMvc.perform(get("/uzenetek"))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void felhasznaloLegfrissebbElolLatja() throws Exception {
        mockMvc.perform(get("/uzenetek").with(user("tesztuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("uzenetek"))
                .andExpect(model().attribute("uzenetek", hasSize(2)))
                .andExpect(model().attribute("uzenetek", contains(
                        hasProperty("targy", is("Új tárgy")),
                        hasProperty("targy", is("Régi tárgy")))))
                // A küldés ideje is megjelenik
                .andExpect(content().string(containsString("2026.10.20. 18:30")))
                .andExpect(content().string(containsString("2026.10.01. 09:05")));
    }

    @Test
    void adminIsLathatja() throws Exception {
        mockMvc.perform(get("/uzenetek").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void uresListaUzenete() throws Exception {
        uzenetRepository.deleteAll();
        mockMvc.perform(get("/uzenetek").with(user("tesztuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Még nem érkezett üzenet.")));
    }
}
