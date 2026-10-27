package com.nje_web.beadando;

import com.nje_web.beadando.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AutentikacioTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void adminLetrejonEsBelephet() throws Exception {
        assertEquals("ADMIN", userRepository.findByUsername("admin").orElseThrow().getRole());

        mockMvc.perform(formLogin("/login").user("admin").password("admin"))
                .andExpect(redirectedUrl("/crud"));
    }

    @Test
    void regisztracioUtanUserkentBelephet() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "tesztuser")
                        .param("password", "titok123")
                        .param("passwordAgain", "titok123"))
                .andExpect(redirectedUrl("/login?registered"));

        assertEquals("USER", userRepository.findByUsername("tesztuser").orElseThrow().getRole());

        mockMvc.perform(formLogin("/login").user("tesztuser").password("titok123"))
                .andExpect(redirectedUrl("/uzenetek"));
    }

    @Test
    void hibasRegisztracioUjraMegjelenikHibakkal() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("username", "admin")
                        .param("password", "123")
                        .param("passwordAgain", "456"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("form", "username", "password", "passwordsMatch"));
    }

    @Test
    void hibasJelszo() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("rossz"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void jogosultsagok() throws Exception {
        // Látogató menüpontjai
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/register")).andExpect(status().isOk());

        // Látogató: Üzenetek -> login, CRUD -> főoldal
        mockMvc.perform(get("/uzenetek")).andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/crud")).andExpect(redirectedUrl("/"));

        // Regisztrált látogató a CRUD-ot nem láthatja
        mockMvc.perform(get("/crud").with(user("bbb").roles("USER"))).andExpect(redirectedUrl("/"));
    }
}
