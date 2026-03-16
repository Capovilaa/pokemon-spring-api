package com.pokemon.api.trainer.infrastructure.web.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainerStatsResponse(
        Long id,
        String username,
        String email,
        Integer wins,
        Integer losses,
        Integer totalBattles,
        Double winRate,
        LocalDateTime createdAt
) {
}