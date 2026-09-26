package com.ecommerce.dto;

import java.math.BigDecimal;

/** category = numele categoriei (frontend-ul il afiseaza direct pe card). */
public record ProductResponse(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal price,
        int stock
) {
}
