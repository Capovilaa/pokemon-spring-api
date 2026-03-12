package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePokemonUseCase extends BaseUseCase<Long, Void> {

    private final PokemonRepository pokemonRepository;

    @Override
    public Void execute(Long id, ExecutionContext context) {
        if (!pokemonRepository.existsByName(id.toString())) {
            pokemonRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Pokemon", id));
        }

        pokemonRepository.deleteById(id);
        return null;
    }
}