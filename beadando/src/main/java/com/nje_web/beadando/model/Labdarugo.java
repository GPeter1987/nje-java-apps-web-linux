package com.nje_web.beadando.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "labdarugo")
public class Labdarugo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer mezszam;

    // N:1 kapcsolat - egy klubnak több labdarúgója lehet
    @ManyToOne
    @JoinColumn(name = "klubid", nullable = false)
    private Klub klub;

    // N:1 kapcsolat - egy poszton több labdarúgó is játszhat
    @ManyToOne
    @JoinColumn(name = "posztid", nullable = false)
    private Poszt poszt;

    // Felvett név esetén üres is lehet
    @Column(length = 50)
    private String utonev;

    @Column(nullable = false, length = 50)
    private String vezeteknev;

    @Column(nullable = false)
    private LocalDate szulido;

    @Column(nullable = false)
    private boolean magyar;

    @Column(nullable = false)
    private boolean kulfoldi;

    // A labdarúgó értéke euróezerben
    @Column(nullable = false)
    private Integer ertek;

    public Labdarugo() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getMezszam() {
        return mezszam;
    }

    public void setMezszam(Integer mezszam) {
        this.mezszam = mezszam;
    }

    public Klub getKlub() {
        return klub;
    }

    public void setKlub(Klub klub) {
        this.klub = klub;
    }

    public Poszt getPoszt() {
        return poszt;
    }

    public void setPoszt(Poszt poszt) {
        this.poszt = poszt;
    }

    public String getUtonev() {
        return utonev;
    }

    public void setUtonev(String utonev) {
        this.utonev = utonev;
    }

    public String getVezeteknev() {
        return vezeteknev;
    }

    public void setVezeteknev(String vezeteknev) {
        this.vezeteknev = vezeteknev;
    }

    public LocalDate getSzulido() {
        return szulido;
    }

    public void setSzulido(LocalDate szulido) {
        this.szulido = szulido;
    }

    public boolean isMagyar() {
        return magyar;
    }

    public void setMagyar(boolean magyar) {
        this.magyar = magyar;
    }

    public boolean isKulfoldi() {
        return kulfoldi;
    }

    public void setKulfoldi(boolean kulfoldi) {
        this.kulfoldi = kulfoldi;
    }

    public Integer getErtek() {
        return ertek;
    }

    public void setErtek(Integer ertek) {
        this.ertek = ertek;
    }

    // Megjelenítéshez: "Vezetéknév Utónév" (ha nincs utónév, csak a vezetéknév)
    @Transient
    public String getTeljesNev() {
        return utonev == null || utonev.isBlank() ? vezeteknev : vezeteknev + " " + utonev;
    }
}
