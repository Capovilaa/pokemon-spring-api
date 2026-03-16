package com.pokemon.api.achievement.application.definition;

import com.pokemon.api.achievement.domain.entity.AchievementType;
import com.pokemon.api.achievement.application.usecase.AchievementContext;

public interface AchievementDefinition {
    AchievementType getType();

    boolean isSatisfied(AchievementContext context);
}