package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.CreatePokemonRequest;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.trainer.application.usecase.FindOrCreateTrainerUseCase;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.type.domain.entity.TypeEntity;
import com.pokemon.api.type.domain.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreatePokemonUseCase extends BaseUseCase<CreatePokemonRequest, PokemonResponse> {

    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;
    private final PokemonMapper pokemonMapper;
    private final FindOrCreateTrainerUseCase findOrCreateTrainerUseCase;

    @Override
    @Transactional
    public PokemonResponse execute(CreatePokemonRequest input, ExecutionContext context) {
        if (pokemonRepository.existsByName(input.name())) {
            throw new ValidationException("Pokemon with name '" + input.name() + "' already exists");
        }

        Set<TypeEntity> types = typeRepository.findAllByIdIn(input.typeIds());
        if (types.size() != input.typeIds().size()) {
            throw new ValidationException("One or more type ids are invalid");
        }
        if (types.size() > 2) {
            throw new ValidationException("A Pokemon cannot have more than 2 types");
        }

        Trainer trainer = findOrCreateTrainerUseCase.execute(null, context);

        Pokemon pokemon = Pokemon.builder()
                .name(input.name())
                .level(input.level())
                .types(types)
                .trainer(trainer)
                .build();

        Pokemon saved = pokemonRepository.save(pokemon);

        return pokemonMapper.toResponse(saved);
    }
}