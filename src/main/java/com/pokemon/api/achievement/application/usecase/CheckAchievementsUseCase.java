package com.pokemon.api.achievement.application.usecase;

import com.pokemon.api.achievement.application.definition.AchievementDefinition;
import com.pokemon.api.achievement.domain.entity.Achievement;
import com.pokemon.api.achievement.domain.repository.AchievementRepository;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckAchievementsUseCase extends BaseUseCase<AchievementContext, Void> {

    private final List<AchievementDefinition> definitions;
    private final AchievementRepository achievementRepository;

    @Override
    public Void execute(AchievementContext context, ExecutionContext executionContext) {
        for (AchievementDefinition definition : definitions) {
            boolean alreadyUnlocked = achievementRepository
                    .existsByTrainerAndType(context.trainer(), definition.getType());

            if (alreadyUnlocked) {
                continue;
            }

            if (definition.isSatisfied(context)) {
                Achievement achievement = Achievement.builder()
                        .trainer(context.trainer())
                        .type(definition.getType())
                        .build();

                achievementRepository.save(achievement);

                log.info("Achievement unlocked: '{}' for trainer '{}'",
                        definition.getType().getTitle(),
                        context.trainer().getUsername());
            }
        }
        return null;
    }
}