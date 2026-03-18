package com.pokemon.api.battle.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ExecuteTurnRequest(
        @NotBlank String moveName
) {
}