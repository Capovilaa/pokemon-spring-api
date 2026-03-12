package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.pokemon.infrastructure.web.dto.UpdatePokemonRequest;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.ForbiddenException;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.type.domain.entity.TypeEntity;
import com.pokemon.api.type.domain.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdatePokemonUseCase extends BaseUseCase<UpdatePokemonUseCase.Input, PokemonResponse> {

    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;
    private final PokemonMapper pokemonMapper;

    public record Input(Long id, UpdatePokemonRequest request) {
    }

    @Override
    @Transactional
    public PokemonResponse execute(Input input, ExecutionContext context) {
        Pokemon pokemon = pokemonRepository.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Pokemon", input.id()));

        boolean isAdmin = context.user().isAdmin();
        boolean isOwner = pokemon.getTrainer() != null &&
                pokemon.getTrainer().getKeycloakId().equals(context.user().id());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("You don't have permission to update this Pokemon");
        }

        boolean nameChanged = !pokemon.getName().equals(input.request().name());
        if (nameChanged && pokemonRepository.existsByName(input.request().name())) {
            throw new ValidationException("Pokemon with name '" + input.request().name() + "' already exists");
        }

        Set<TypeEntity> types = typeRepository.findAllByIdIn(input.request().typeIds());
        if (types.size() != input.request().typeIds().size()) {
            throw new ValidationException("One or more type ids are invalid");
        }
        if (types.size() > 2) {
            throw new ValidationException("A Pokemon cannot have more than 2 types");
        }

        pokemon.setName(input.request().name());
        pokemon.setLevel(input.request().level());
        pokemon.setTypes(types);

        Pokemon updated = pokemonRepository.save(pokemon);

        return pokemonMapper.toResponse(updated);
    }
}