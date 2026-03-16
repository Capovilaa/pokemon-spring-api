package com.pokemon.api.pokemon.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePokemonRequest(
        @NotBlank String pokemonName,
        @NotNull @Min(1) Integer level
) {
}