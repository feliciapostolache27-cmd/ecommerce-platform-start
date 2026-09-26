package com.ecommerce.dto;

import com.ecommerce.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** PUT inlocuieste complet task-ul, de aceea statusul este obligatoriu. */
public record UpdateTaskRequest(
        @Schema(example = "Prezentare finalizata")
        @NotBlank(message = "Titlul este obligatoriu")
        @Size(max = 150, message = "Titlul poate avea maxim 150 de caractere")
        String title,

        @Size(max = 2000, message = "Descrierea poate avea maxim 2000 de caractere")
        String description,

        @Schema(example = "DONE")
        @NotNull(message = "Statusul este obligatoriu")
        TaskStatus status
) {
}
