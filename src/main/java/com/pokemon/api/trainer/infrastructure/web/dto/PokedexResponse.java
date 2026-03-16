package com.pokemon.api.trainer.infrastructure.web.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PokedexResponse(
        int totalCaught,
        int totalPossible,
        double completionPercentage,
        List<PokedexEntryResponse> entries
) {
}