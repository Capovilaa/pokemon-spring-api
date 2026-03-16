package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.cache.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePokemonUseCase extends BaseUseCase<Long, Void> {

    private final PokemonRepository pokemonRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.POKEMON_CACHE, key = "#input"),
            @CacheEvict(value = CacheConfig.POKEMON_LIST_CACHE, key = "'all'")
    })
    public Void execute(Long input, ExecutionContext context) {
        pokemonRepository.findById(input)
                .orElseThrow(() -> new NotFoundException("Pokemon", input));

        pokemonRepository.deleteById(input);
        return null;
    }
}