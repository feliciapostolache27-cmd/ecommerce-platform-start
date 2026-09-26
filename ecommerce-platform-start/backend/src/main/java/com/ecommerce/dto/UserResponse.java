package com.ecommerce.dto;

import com.ecommerce.model.Role;

/** Forma expusa in API: fara password_hash. */
public record UserResponse(Long id, String email, Role role) {
}
