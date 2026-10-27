package com.nje_web.beadando;

import com.nje_web.beadando.model.Labdarugo;
import com.nje_web.beadando.repository.KlubRepository;
import com.nje_web.beadando.repository.LabdarugoRepository;
import com.nje_web.beadando.repository.PosztRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class Nb1AdatokTests {

    @Autowired
    private KlubRepository klubRepository;

    @Autowired
    private PosztRepository posztRepository;

    @Autowired
    private LabdarugoRepository labdarugoRepository;

    @Test
    void nb1AdatokBetoltodnek() {
        assertEquals(12, klubRepository.count());
        assertEquals(14, posztRepository.count());
        assertEquals(318, labdarugoRepository.count());

        Labdarugo elso = labdarugoRepository.findById(1).orElseThrow();
        assertEquals("Haris Attila", elso.getTeljesNev());
        assertEquals("Balmazújváros FC", elso.getKlub().getCsapatnev());
        assertEquals(LocalDate.of(1997, 1, 23), elso.getSzulido());
        assertTrue(elso.isMagyar());
    }

    @Test
    void ujLabdarugoMentheto() {
        Labdarugo uj = new Labdarugo();
        uj.setMezszam(99);
        uj.setKlub(klubRepository.findById(1).orElseThrow());
        uj.setPoszt(posztRepository.findById(9).orElseThrow());
        uj.setVezeteknev("Teszt");
        uj.setUtonev("Elek");
        uj.setSzulido(LocalDate.of(2000, 1, 1));
        uj.setMagyar(true);
        uj.setErtek(100);

        Labdarugo mentett = labdarugoRepository.save(uj);
        assertTrue(mentett.getId() > 318);
    }
}
