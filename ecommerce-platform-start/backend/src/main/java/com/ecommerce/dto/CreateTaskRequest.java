package com.ecommerce.dto;

import com.ecommerce.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @Schema(example = "Pregatesc prezentarea")
        @NotBlank(message = "Titlul este obligatoriu")
        @Size(max = 150, message = "Titlul poate avea maxim 150 de caractere")
        String title,

        @Schema(example = "Slide-uri pentru atestare")
        @Size(max = 2000, message = "Descrierea poate avea maxim 2000 de caractere")
        String description,

        @Schema(description = "Optional; implicit TODO", example = "TODO")
        TaskStatus status
) {
}
