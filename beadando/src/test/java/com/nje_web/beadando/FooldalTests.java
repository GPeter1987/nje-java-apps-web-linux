package com.nje_web.beadando;

import com.nje_web.beadando.dto.KlubOsszesites;
import com.nje_web.beadando.model.Labdarugo;
import com.nje_web.beadando.service.FooldalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class FooldalTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FooldalService fooldalService;

    @Test
    void klubOsszesitesek() {
        List<Labdarugo> labdarugok = fooldalService.osszesLabdarugo();
        List<KlubOsszesites> klubok = fooldalService.klubOsszesitesek(labdarugok);

        assertEquals(12, klubok.size());
        assertEquals(labdarugok.size(), klubok.stream().mapToLong(KlubOsszesites::jatekosokSzama).sum());
        assertEquals(fooldalService.osszertek(labdarugok), klubok.stream().mapToLong(KlubOsszesites::osszertek).sum());
        // Keretérték szerint csökkenő sorrend
        assertTrue(klubok.get(0).osszertek() >= klubok.get(11).osszertek());
    }

    @Test
    void legertekesebbJatekosokCsokkenoSorrendben() {
        List<Labdarugo> top = fooldalService.legertekesebbek(fooldalService.osszesLabdarugo(), 5);

        assertEquals(5, top.size());
        for (int i = 1; i < top.size(); i++) {
            assertTrue(top.get(i - 1).getErtek() >= top.get(i).getErtek());
        }
    }

    @Test
    void fooldalMegjelenik() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("klubok", "legertekesebbek", "osszertek", "kulfoldiArany"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ferencvárosi TC")));
    }
}
