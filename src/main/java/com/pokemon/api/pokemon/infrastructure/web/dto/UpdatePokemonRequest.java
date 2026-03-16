package com.pokemon.api.pokemon.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePokemonRequest(
        @NotBlank String name,
        @NotNull @Min(1) Integer level
) {
}