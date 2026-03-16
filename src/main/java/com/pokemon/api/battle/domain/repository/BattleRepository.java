package com.pokemon.api.battle.domain.repository;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.trainer.domain.entity.Trainer;

import java.util.List;

public interface BattleRepository {
    Battle save(Battle battle);

    List<Battle> findByTrainer(Trainer trainer);
}