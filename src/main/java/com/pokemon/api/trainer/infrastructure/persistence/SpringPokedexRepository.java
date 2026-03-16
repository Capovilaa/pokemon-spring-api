package com.pokemon.api.trainer.infrastructure.persistence;

import com.pokemon.api.trainer.domain.entity.PokedexEntry;
import com.pokemon.api.trainer.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringPokedexRepository extends JpaRepository<PokedexEntry, Long> {
    boolean existsByTrainerAndSpeciesId(Trainer trainer, Integer speciesId);

    List<PokedexEntry> findByTrainer(Trainer trainer);

    long countByTrainer(Trainer trainer);
}