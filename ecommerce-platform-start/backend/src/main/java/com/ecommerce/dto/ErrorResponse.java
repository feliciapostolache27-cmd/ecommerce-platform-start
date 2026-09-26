package com.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/** Raspuns de eroare uniform. "fields" apare doar la erori de validare. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, Map<String, String> fields) {

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }
}
