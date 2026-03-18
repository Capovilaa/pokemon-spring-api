package com.pokemon.api.oak.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OakQuestionRequest(
        @NotBlank @Size(max = 1000) String question,
        String conversationId
) {
}