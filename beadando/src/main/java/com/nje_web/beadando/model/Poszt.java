package com.nje_web.beadando.model;

import jakarta.persistence.*;

@Entity
@Table(name = "poszt")
public class Poszt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nev;

    public Poszt() {
    }

    public Poszt(String nev) {
        this.nev = nev;
    }

    public Integer getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }
}
