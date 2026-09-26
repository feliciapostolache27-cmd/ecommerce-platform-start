package com.ecommerce.security;

import com.ecommerce.model.Role;

/** Userul curent, reconstruit din JWT la fiecare cerere (fara interogare in baza de date). */
public record AuthUser(Long id, String email, Role role) {
}
