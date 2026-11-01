package com.nje_web.beadando.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// A regisztrációs űrlap adatai szerver oldali validációval
public class RegisztracioForm {

    @NotBlank(message = "A felhasználónév megadása kötelező.")
    @Size(min = 3, max = 30, message = "A felhasználónév 3-30 karakter hosszú lehet.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "A felhasználónév csak betűt, számot, pontot, kötőjelet és aláhúzást tartalmazhat.")
    private String username;

    @NotBlank(message = "A jelszó megadása kötelező.")
    @Size(min = 6, max = 64, message = "A jelszó 6-64 karakter hosszú lehet.")
    private String password;

    @NotBlank(message = "A jelszó megerősítése kötelező.")
    private String passwordAgain;

    @AssertTrue(message = "A két jelszó nem egyezik.")
    public boolean isPasswordsMatch() {
        return password == null || password.equals(passwordAgain);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }
}
