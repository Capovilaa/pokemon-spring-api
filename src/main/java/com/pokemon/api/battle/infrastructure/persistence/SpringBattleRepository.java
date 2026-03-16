package com.pokemon.api.battle.infrastructure.persistence;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.trainer.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringBattleRepository extends JpaRepository<Battle, Long> {

    @Query("SELECT b FROM Battle b WHERE b.attackerTrainer = :trainer OR b.defenderTrainer = :trainer")
    List<Battle> findByTrainer(@Param("trainer") Trainer trainer);
}