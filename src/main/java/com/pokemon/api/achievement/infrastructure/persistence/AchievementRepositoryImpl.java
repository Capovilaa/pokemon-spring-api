package com.pokemon.api.achievement.infrastructure.persistence;

import com.pokemon.api.achievement.domain.entity.Achievement;
import com.pokemon.api.achievement.domain.entity.AchievementType;
import com.pokemon.api.achievement.domain.repository.AchievementRepository;
import com.pokemon.api.trainer.domain.entity.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AchievementRepositoryImpl implements AchievementRepository {

    private final SpringAchievementRepository springAchievementRepository;

    @Override
    public boolean existsByTrainerAndType(Trainer trainer, AchievementType type) {
        return springAchievementRepository.existsByTrainerAndType(trainer, type);
    }

    @Override
    public Achievement save(Achievement achievement) {
        return springAchievementRepository.save(achievement);
    }

    @Override
    public List<Achievement> findByTrainer(Trainer trainer) {
        return springAchievementRepository.findByTrainer(trainer);
    }
}