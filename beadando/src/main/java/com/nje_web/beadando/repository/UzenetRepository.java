package com.nje_web.beadando.repository;

import com.nje_web.beadando.model.Uzenet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UzenetRepository extends JpaRepository<Uzenet, Long> {
    // Az Üzenetek menühöz: a legfrissebb elöl
    List<Uzenet> findAllByOrderByKuldesIdejeDesc();
}
