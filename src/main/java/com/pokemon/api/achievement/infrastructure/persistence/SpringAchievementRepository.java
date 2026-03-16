package com.pokemon.api.achievement.infrastructure.persistence;

import com.pokemon.api.achievement.domain.entity.Achievement;
import com.pokemon.api.achievement.domain.entity.AchievementType;
import com.pokemon.api.trainer.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringAchievementRepository extends JpaRepository<Achievement, Long> {
    boolean existsByTrainerAndType(Trainer trainer, AchievementType type);

    List<Achievement> findByTrainer(Trainer trainer);
}