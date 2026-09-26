package com.ecommerce.dto;

import com.ecommerce.model.Role;

/** Forma asteptata de frontend: { token, email, role }. */
public record AuthResponse(String token, String email, Role role) {
}
