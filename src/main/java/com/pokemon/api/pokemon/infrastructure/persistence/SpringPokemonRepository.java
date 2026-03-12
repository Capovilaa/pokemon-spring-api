package com.pokemon.api.pokemon.infrastructure.persistence;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringPokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByName(String name);

    boolean existsByName(String name);
}