package com.pokemon.api.trainer.infrastructure.web.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainerResponse(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
}