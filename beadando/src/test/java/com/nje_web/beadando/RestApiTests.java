package com.nje_web.beadando;

import com.nje_web.beadando.repository.LabdarugoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class RestApiTests {

    private static final String JATEKOS = """
            {"mezszam":99,"klubId":1,"posztId":9,"vezeteknev":"Teszt","szulido":"2000-01-15","ertek":100}""";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabdarugoRepository labdarugoRepository;

    @Test
    void olvasasAzonositasNelkul() throws Exception {
        mockMvc.perform(get("/api/labdarugok/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teljesNev").value("Haris Attila"));
    }

    @Test
    void irasAzonositasNelkul401() throws Exception {
        mockMvc.perform(delete("/api/labdarugok/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
        assertTrue(labdarugoRepository.existsById(1));
    }

    @Test
    void irasNemAdminnal403() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                .param("username", "apiuser").param("password", "titok123").param("passwordAgain", "titok123"));

        mockMvc.perform(delete("/api/labdarugok/1").with(httpBasic("apiuser", "titok123")))
                .andExpect(status().isForbidden());
        assertTrue(labdarugoRepository.existsById(1));
    }

    @Test
    void adminLetrehozzaEsTorli() throws Exception {
        // A magyar/kulfoldi mező elhagyható: false lesz
        String location = mockMvc.perform(post("/api/labdarugok").with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(JATEKOS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.magyar").value(false))
                .andExpect(jsonPath("$.kulfoldi").value(false))
                .andReturn().getResponse().getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);
        mockMvc.perform(delete("/api/labdarugok/" + id).with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());
        assertFalse(labdarugoRepository.existsById(Integer.valueOf(id)));
    }

    @Test
    void hibasJsonSajatHibauzenettel() throws Exception {
        mockMvc.perform(post("/api/labdarugok").with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content("{rossz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.uzenet").exists());
    }

    @Test
    void postmanGyujtemenyLetoltheto() throws Exception {
        mockMvc.perform(get("/docs/NB1-Labdarugo-API.postman_collection.json"))
                .andExpect(status().isOk());
    }

    @Test
    void restOldalAktualisCimmel() throws Exception {
        mockMvc.perform(get("/rest"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("http://localhost/api/labdarugok")));
    }
}
