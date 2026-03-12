package com.pokemon.api.pokemon.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.Set;

@Builder
public record UpdatePokemonRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Level is required")
        @Min(value = 1, message = "Level must be at least 1")
        @Max(value = 100, message = "Level must be at most 100")
        Integer level,

        @NotNull(message = "Types are required")
        @Size(min = 1, message = "At least one type is required")
        Set<Long> typeIds
) {
}