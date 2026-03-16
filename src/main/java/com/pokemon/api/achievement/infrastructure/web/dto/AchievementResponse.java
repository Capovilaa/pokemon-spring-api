package com.pokemon.api.achievement.infrastructure.web.dto;

import com.pokemon.api.achievement.domain.entity.AchievementType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AchievementResponse(
        AchievementType type,
        String title,
        String description,
        LocalDateTime unlockedAt
) {
}