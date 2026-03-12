package com.pokemon.api.type.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateTypeRequest(

        @NotBlank(message = "Name is required")
        String name
) {
}