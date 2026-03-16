package com.pokemon.api.trainer.infrastructure.persistence;

import com.pokemon.api.trainer.domain.entity.PokedexEntry;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.PokedexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PokedexRepositoryImpl implements PokedexRepository {

    private final SpringPokedexRepository springPokedexRepository;

    @Override
    public boolean existsByTrainerAndSpeciesId(Trainer trainer, Integer speciesId) {
        return springPokedexRepository.existsByTrainerAndSpeciesId(trainer, speciesId);
    }

    @Override
    public PokedexEntry save(PokedexEntry entry) {
        return springPokedexRepository.save(entry);
    }

    @Override
    public List<PokedexEntry> findByTrainer(Trainer trainer) {
        return springPokedexRepository.findByTrainer(trainer);
    }

    @Override
    public long countByTrainer(Trainer trainer) {
        return springPokedexRepository.countByTrainer(trainer);
    }
}