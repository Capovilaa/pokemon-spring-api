package com.pokemon.api.pokemon.infrastructure.persistence;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.trainer.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringPokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByName(String name);

    boolean existsByName(String name);

    List<Pokemon> findByTrainer(Trainer trainer);
}