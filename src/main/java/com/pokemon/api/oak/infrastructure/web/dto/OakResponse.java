package com.pokemon.api.oak.infrastructure.web.dto;

import lombok.Builder;

@Builder
public record OakResponse(
        String answer,
        String model,
        Integer inputTokens,
        Integer outputTokens
) {
}