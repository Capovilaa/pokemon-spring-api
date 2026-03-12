package com.pokemon.api.pokemon.infrastructure.persistence;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PokemonRepositoryImpl implements PokemonRepository {

    private final SpringPokemonRepository springPokemonRepository;

    @Override
    public Pokemon save(Pokemon pokemon) {
        return springPokemonRepository.save(pokemon);
    }

    @Override
    public Optional<Pokemon> findById(Long id) {
        return springPokemonRepository.findById(id);
    }

    @Override
    public Optional<Pokemon> findByName(String name) {
        return springPokemonRepository.findByName(name);
    }

    @Override
    public List<Pokemon> findAll() {
        return springPokemonRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        springPokemonRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return springPokemonRepository.existsByName(name);
    }
}