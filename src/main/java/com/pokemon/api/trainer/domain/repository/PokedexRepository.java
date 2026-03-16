package com.pokemon.api.trainer.domain.repository;

import com.pokemon.api.trainer.domain.entity.PokedexEntry;
import com.pokemon.api.trainer.domain.entity.Trainer;

import java.util.List;

public interface PokedexRepository {
    boolean existsByTrainerAndSpeciesId(Trainer trainer, Integer speciesId);

    PokedexEntry save(PokedexEntry entry);

    List<PokedexEntry> findByTrainer(Trainer trainer);

    long countByTrainer(Trainer trainer);
}