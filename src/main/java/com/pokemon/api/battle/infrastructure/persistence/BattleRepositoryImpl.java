package com.pokemon.api.battle.infrastructure.persistence;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.trainer.domain.entity.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BattleRepositoryImpl implements BattleRepository {

    private final SpringBattleRepository springBattleRepository;

    @Override
    public Battle save(Battle battle) {
        return springBattleRepository.save(battle);
    }

    @Override
    public List<Battle> findByTrainer(Trainer trainer) {
        return springBattleRepository.findByTrainer(trainer);
    }

    @Override
    public Optional<Battle> findById(Long id) {
        return springBattleRepository.findById(id);
    }
}