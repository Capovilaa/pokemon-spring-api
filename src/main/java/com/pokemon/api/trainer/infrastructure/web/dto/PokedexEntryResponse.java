package com.pokemon.api.trainer.infrastructure.web.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PokedexEntryResponse(
        Integer speciesId,
        String speciesName,
        String spriteUrl,
        LocalDateTime caughtAt
) {
}