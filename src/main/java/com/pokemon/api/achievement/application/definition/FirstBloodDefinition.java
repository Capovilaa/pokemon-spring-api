package com.pokemon.api.achievement.application.definition;

import com.pokemon.api.achievement.application.usecase.AchievementContext;
import com.pokemon.api.achievement.domain.entity.AchievementType;
import org.springframework.stereotype.Component;

@Component
public class FirstBloodDefinition implements AchievementDefinition {
    @Override
    public AchievementType getType() {
        return AchievementType.FIRST_BLOOD;
    }

    @Override
    public boolean isSatisfied(AchievementContext context) {
        return context.totalWins() >= 1;
    }
}