package com.pokemon.api.type.infraestructure.web.dto;

import lombok.Builder;

@Builder
public record TypeResponse(
        Long id,
        String name
) {
}