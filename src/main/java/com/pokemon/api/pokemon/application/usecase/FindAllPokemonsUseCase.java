package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.cache.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAllPokemonsUseCase extends BaseUseCase<Void, List<PokemonResponse>> {

    private final PokemonRepository pokemonRepository;
    private final PokemonMapper pokemonMapper;

    @Override
    @Cacheable(value = CacheConfig.POKEMON_LIST_CACHE, key = "'all'")
    public List<PokemonResponse> execute(Void input, ExecutionContext context) {
        return pokemonRepository.findAll()
                .stream()
                .map(pokemonMapper::toResponse)
                .toList();
    }
}