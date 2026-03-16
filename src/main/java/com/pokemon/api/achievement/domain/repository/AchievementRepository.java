package com.pokemon.api.achievement.domain.repository;

import com.pokemon.api.achievement.domain.entity.Achievement;
import com.pokemon.api.achievement.domain.entity.AchievementType;
import com.pokemon.api.trainer.domain.entity.Trainer;

import java.util.List;

public interface AchievementRepository {
    boolean existsByTrainerAndType(Trainer trainer, AchievementType type);

    Achievement save(Achievement achievement);

    List<Achievement> findByTrainer(Trainer trainer);
}