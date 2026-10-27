package com.nje_web.beadando.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// A Kapcsolat űrlapon beküldött üzenet
@Entity
@Table(name = "uzenet")
public class Uzenet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nev;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String targy;

    @Column(nullable = false, length = 2000)
    private String szoveg;

    @Column(name = "kuldes_ideje", nullable = false)
    private LocalDateTime kuldesIdeje;

    // A küldő felhasználóneve, ha bejelentkezve küldte (látogatónál null)
    @Column(length = 30)
    private String felhasznalo;

    public Uzenet() {
    }

    public Uzenet(String nev, String email, String targy, String szoveg, String felhasznalo) {
        this.nev = nev;
        this.email = email;
        this.targy = targy;
        this.szoveg = szoveg;
        this.felhasznalo = felhasznalo;
    }

    // A küldés időpontját mentéskor a szerver állítja be
    @PrePersist
    void kuldesIdejeBeallitasa() {
        if (kuldesIdeje == null) {
            kuldesIdeje = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTargy() {
        return targy;
    }

    public void setTargy(String targy) {
        this.targy = targy;
    }

    public String getSzoveg() {
        return szoveg;
    }

    public void setSzoveg(String szoveg) {
        this.szoveg = szoveg;
    }

    public LocalDateTime getKuldesIdeje() {
        return kuldesIdeje;
    }

    public void setKuldesIdeje(LocalDateTime kuldesIdeje) {
        this.kuldesIdeje = kuldesIdeje;
    }

    public String getFelhasznalo() {
        return felhasznalo;
    }

    public void setFelhasznalo(String felhasznalo) {
        this.felhasznalo = felhasznalo;
    }
}
