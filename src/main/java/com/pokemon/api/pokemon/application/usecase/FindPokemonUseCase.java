package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.cache.CacheConfig;
import com.pokemon.api.shared.infrastructure.pokeapi.EvolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindPokemonUseCase extends BaseUseCase<Long, PokemonResponse> {

    private final PokemonRepository pokemonRepository;
    private final PokemonMapper pokemonMapper;
    private final EvolutionService evolutionService;

    @Override
    @Cacheable(value = CacheConfig.POKEMON_CACHE, key = "#input")
    public PokemonResponse execute(Long input, ExecutionContext context) {
        Pokemon pokemon = pokemonRepository.findById(input)
                .orElseThrow(() -> new NotFoundException("Pokemon", input));

        String nextEvolution = evolutionService
                .getNextEvolution(pokemon.getName())
                .orElse(null);

        return pokemonMapper.toResponse(pokemon, nextEvolution);
    }
}