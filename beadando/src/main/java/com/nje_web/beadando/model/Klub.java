package com.nje_web.beadando.model;

import jakarta.persistence.*;

@Entity
@Table(name = "klub")
public class Klub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String csapatnev;

    public Klub() {
    }

    public Klub(String csapatnev) {
        this.csapatnev = csapatnev;
    }

    public Integer getId() {
        return id;
    }

    public String getCsapatnev() {
        return csapatnev;
    }

    public void setCsapatnev(String csapatnev) {
        this.csapatnev = csapatnev;
    }
}
