package com.nje_web.beadando.dto;

import com.nje_web.beadando.model.Klub;

// Egy klub összesített adatai a főoldali csempékhez
public record KlubOsszesites(Klub klub, long jatekosokSzama, long osszertek, String kep) {
}
