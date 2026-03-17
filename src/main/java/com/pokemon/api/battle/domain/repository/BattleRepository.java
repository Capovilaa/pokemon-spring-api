package com.pokemon.api.battle.domain.repository;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.trainer.domain.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface BattleRepository {
    Battle save(Battle battle);

    List<Battle> findByTrainer(Trainer trainer);

    Optional<Battle> findById(Long id);
}