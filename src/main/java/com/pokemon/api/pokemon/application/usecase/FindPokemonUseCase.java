package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindPokemonUseCase extends BaseUseCase<Long, PokemonResponse> {

    private final PokemonRepository pokemonRepository;
    private final PokemonMapper pokemonMapper;

    @Override
    public PokemonResponse execute(Long id, ExecutionContext context) {
        return pokemonRepository.findById(id)
                .map(pokemonMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Pokemon", id));
    }
}