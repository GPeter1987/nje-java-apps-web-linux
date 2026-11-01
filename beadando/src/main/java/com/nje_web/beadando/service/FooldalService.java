package com.nje_web.beadando.service;

import com.nje_web.beadando.dto.KlubOsszesites;
import com.nje_web.beadando.model.Klub;
import com.nje_web.beadando.model.Labdarugo;
import com.nje_web.beadando.repository.KlubRepository;
import com.nje_web.beadando.repository.LabdarugoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// A főoldal statisztikái mindhárom tábla (klub, poszt, labdarugo) adataiból
@Service
@Transactional(readOnly = true)
public class FooldalService {

    private final KlubRepository klubRepository;
    private final LabdarugoRepository labdarugoRepository;

    public FooldalService(KlubRepository klubRepository, LabdarugoRepository labdarugoRepository) {
        this.klubRepository = klubRepository;
        this.labdarugoRepository = labdarugoRepository;
    }

    public List<Labdarugo> osszesLabdarugo() {
        return labdarugoRepository.findAll();
    }

    // Klubonkénti játékosszám és keretérték, keretérték szerint csökkenő sorrendben
    public List<KlubOsszesites> klubOsszesitesek(List<Labdarugo> labdarugok) {
        Map<Integer, List<Labdarugo>> klubonkent = labdarugok.stream()
                .collect(Collectors.groupingBy(l -> l.getKlub().getId()));

        return klubRepository.findAll().stream()
                .map(klub -> osszesites(klub, klubonkent.getOrDefault(klub.getId(), List.of())))
                .sorted(Comparator.comparingLong(KlubOsszesites::osszertek).reversed())
                .toList();
    }

    // A legértékesebb játékosok
    public List<Labdarugo> legertekesebbek(List<Labdarugo> labdarugok, int darab) {
        return labdarugok.stream()
                .sorted(Comparator.comparing(Labdarugo::getErtek).reversed()
                        .thenComparing(Labdarugo::getVezeteknev))
                .limit(darab)
                .toList();
    }

    public long osszertek(List<Labdarugo> labdarugok) {
        return labdarugok.stream().mapToLong(Labdarugo::getErtek).sum();
    }

    // Külföldi állampolgárságú játékosok aránya százalékban
    public long kulfoldiArany(List<Labdarugo> labdarugok) {
        if (labdarugok.isEmpty()) {
            return 0;
        }
        long kulfoldiek = labdarugok.stream().filter(Labdarugo::isKulfoldi).count();
        return Math.round(kulfoldiek * 100.0 / labdarugok.size());
    }

    // Posztonkénti játékosszám alapján a legnépesebb poszt neve
    public String legnepesebbPoszt(List<Labdarugo> labdarugok) {
        return labdarugok.stream()
                .collect(Collectors.groupingBy(l -> l.getPoszt().getNev(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("-");
    }

    private KlubOsszesites osszesites(Klub klub, List<Labdarugo> jatekosok) {
        return new KlubOsszesites(klub, jatekosok.size(), osszertek(jatekosok),
                "/images/nb1/klub-" + klub.getId() + ".jpg");
    }
}
