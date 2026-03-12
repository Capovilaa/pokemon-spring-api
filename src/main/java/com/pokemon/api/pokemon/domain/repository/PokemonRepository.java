package com.pokemon.api.pokemon.domain.repository;

import com.pokemon.api.pokemon.domain.entity.Pokemon;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository {

    Pokemon save(Pokemon pokemon);

    Optional<Pokemon> findById(Long id);

    Optional<Pokemon> findByName(String name);

    List<Pokemon> findAll();

    void deleteById(Long id);

    boolean existsByName(String name);
}