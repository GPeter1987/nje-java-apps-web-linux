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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class KapcsolatTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UzenetRepository uzenetRepository;

    @BeforeEach
    void torles() {
        uzenetRepository.deleteAll();
    }

    @Test
    void latogatoIsMegnyithatja() throws Exception {
        mockMvc.perform(get("/kapcsolat"))
                .andExpect(status().isOk())
                .andExpect(view().name("kapcsolat"));
    }

    @Test
    void ervenyesUzenetMentodik() throws Exception {
        mockMvc.perform(post("/kapcsolat").with(csrf())
                        .param("nev", " Teszt Elek ")
                        .param("email", "teszt@example.com")
                        .param("targy", "Kérdés")
                        .param("szoveg", "Mikor frissül az adatbázis?"))
                .andExpect(redirectedUrl("/kapcsolat"))
                .andExpect(flash().attribute("siker", true));

        List<Uzenet> uzenetek = uzenetRepository.findAll();
        assertEquals(1, uzenetek.size());
        Uzenet uzenet = uzenetek.get(0);
        assertEquals("Teszt Elek", uzenet.getNev());
        assertNotNull(uzenet.getKuldesIdeje());
        assertNull(uzenet.getFelhasznalo());
    }

    @Test
    void bejelentkezveAFelhasznalonevIsMentodik() throws Exception {
        mockMvc.perform(post("/kapcsolat").with(csrf()).with(user("tesztuser").roles("USER"))
                        .param("nev", "Teszt Elek")
                        .param("email", "teszt@example.com")
                        .param("targy", "Kérdés")
                        .param("szoveg", "Mikor frissül az adatbázis?"))
                .andExpect(redirectedUrl("/kapcsolat"));

        assertEquals("tesztuser", uzenetRepository.findAll().get(0).getFelhasznalo());
    }

    @Test
    void hibasUrlapNemMentodik() throws Exception {
        mockMvc.perform(post("/kapcsolat").with(csrf())
                        .param("nev", "")
                        .param("email", "nem-email")
                        .param("targy", "ab")
                        .param("szoveg", "rövid"))
                .andExpect(status().isOk())
                .andExpect(view().name("kapcsolat"))
                .andExpect(model().attributeHasFieldErrors("form", "nev", "email", "targy", "szoveg"))
                // Üres mezőnél csak egy hibaüzenet ("kötelező") jelenik meg
                .andExpect(model().attributeErrorCount("form", 4));

        assertEquals(0, uzenetRepository.count());
    }

    @Test
    void csrfTokenNelkulElutasitva() throws Exception {
        mockMvc.perform(post("/kapcsolat")
                        .param("nev", "Teszt Elek")
                        .param("email", "teszt@example.com")
                        .param("targy", "Kérdés")
                        .param("szoveg", "Mikor frissül az adatbázis?"))
                // A WebSecurityConfig accessDeniedHandler-e a főoldalra irányít (403 helyett)
                .andExpect(redirectedUrl("/"));

        assertEquals(0, uzenetRepository.count());
    }

    @Test
    void legfrissebbElol() {
        Uzenet regi = new Uzenet("A", "a@example.com", "Régi", "Régebbi üzenet", null);
        regi.setKuldesIdeje(java.time.LocalDateTime.now().minusDays(1));
        uzenetRepository.save(regi);
        uzenetRepository.save(new Uzenet("B", "b@example.com", "Új", "Frissebb üzenet", null));

        List<Uzenet> rendezett = uzenetRepository.findAllByOrderByKuldesIdejeDesc();
        assertEquals("Új", rendezett.get(0).getTargy());
        assertEquals("Régi", rendezett.get(1).getTargy());
    }
}
