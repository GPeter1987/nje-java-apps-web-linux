package com.nje_web.beadando.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// A Kapcsolat űrlap adatai szerver oldali validációval
public class KapcsolatForm {

    @NotBlank(message = "A név megadása kötelező.")
    @Size(min = 2, max = 100, message = "A név 2-100 karakter hosszú lehet.")
    private String nev;

    @NotBlank(message = "Az e-mail cím megadása kötelező.")
    @Email(message = "Érvénytelen e-mail cím.")
    @Size(max = 100, message = "Az e-mail cím legfeljebb 100 karakter lehet.")
    private String email;

    @NotBlank(message = "A tárgy megadása kötelező.")
    @Size(min = 3, max = 100, message = "A tárgy 3-100 karakter hosszú lehet.")
    private String targy;

    @NotBlank(message = "Az üzenet megadása kötelező.")
    @Size(min = 10, max = 2000, message = "Az üzenet 10-2000 karakter hosszú lehet.")
    private String szoveg;

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
}
