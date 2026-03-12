package com.pokemon.api.type.infrastructure.web.dto;

import lombok.Builder;

@Builder
public record TypeResponse(
        Long id,
        String name
) {
}