package com.pokemon.api.achievement.application.usecase;

import com.pokemon.api.achievement.domain.repository.AchievementRepository;
import com.pokemon.api.achievement.infrastructure.web.dto.AchievementResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyAchievementsUseCase extends BaseUseCase<Void, List<AchievementResponse>> {

    private final AchievementRepository achievementRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public List<AchievementResponse> execute(Void input, ExecutionContext context) {
        var trainer = trainerRepository
                .findByKeycloakId(context.user().id())
                .orElseThrow(() -> new NotFoundException("Trainer", context.user().id()));

        return achievementRepository.findByTrainer(trainer)
                .stream()
                .map(a -> AchievementResponse.builder()
                        .type(a.getType())
                        .title(a.getType().getTitle())
                        .description(a.getType().getDescription())
                        .unlockedAt(a.getUnlockedAt())
                        .build())
                .toList();
    }
}