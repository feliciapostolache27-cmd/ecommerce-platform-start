package com.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email-ul este obligatoriu")
        @Email(message = "Email invalid")
        String email,

        @NotBlank(message = "Parola este obligatorie")
        @Size(min = 6, max = 72, message = "Parola trebuie sa aiba intre 6 si 72 de caractere")
        String password
) {
}
