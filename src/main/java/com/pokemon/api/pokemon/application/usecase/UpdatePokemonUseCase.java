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
import com.pokemon.api.shared.infrastructure.cache.CacheConfig;
import com.pokemon.api.shared.infrastructure.pokeapi.EvolutionService;
import com.pokemon.api.shared.infrastructure.pokeapi.PokeApiClient;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiPokemonResponse;
import com.pokemon.api.type.domain.entity.TypeEntity;
import com.pokemon.api.type.domain.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePokemonUseCase extends BaseUseCase<UpdatePokemonUseCase.Input, PokemonResponse> {

    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;
    private final PokemonMapper pokemonMapper;
    private final EvolutionService evolutionService;
    private final PokeApiClient pokeApiClient;

    public record Input(Long id, UpdatePokemonRequest request) {
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.POKEMON_CACHE, key = "#input.id"),
            @CacheEvict(value = CacheConfig.POKEMON_LIST_CACHE, key = "'all'")
    })
    public PokemonResponse execute(Input input, ExecutionContext context) {
        Pokemon pokemon = pokemonRepository.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Pokemon", input.id()));

        boolean isAdmin = context.user().isAdmin();
        boolean isOwner = pokemon.getTrainer() != null &&
                pokemon.getTrainer().getKeycloakId().equals(context.user().id());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("You don't have permission to update this Pokemon");
        }

        pokemon.setLevel(input.request().level());

        // Verifica evolução automática
        String evolutionName = evolutionService
                .getEvolution(pokemon.getName(), input.request().level())
                .orElse(null);

        if (evolutionName != null) {
            log.info("Pokemon '{}' is evolving to '{}'!", pokemon.getName(), evolutionName);
            applyEvolution(pokemon, evolutionName);
        } else {
            // Sem evolução — apenas atualiza o level
            boolean nameChanged = !pokemon.getName().equalsIgnoreCase(input.request().name());
            if (nameChanged && pokemonRepository.existsByName(input.request().name())) {
                throw new ValidationException("Pokemon with name '" + input.request().name() + "' already exists");
            }
            pokemon.setName(input.request().name());
        }

        String nextEvolution = evolutionService
                .getNextEvolution(pokemon.getName())
                .orElse(null);

        return pokemonMapper.toResponse(pokemonRepository.save(pokemon), nextEvolution);
    }

    private void applyEvolution(Pokemon pokemon, String evolutionName) {
        PokeApiPokemonResponse pokeData = pokeApiClient.fetchPokemon(evolutionName);

        Set<TypeEntity> types = pokeData.types().stream()
                .map(slot -> findOrCreateType(slot.type().name()))
                .collect(Collectors.toSet());

        pokemon.setName(capitalize(pokeData.name()));
        pokemon.setSpeciesId(pokeData.id());
        pokemon.setSpriteUrl(pokeData.sprites().frontDefault());
        pokemon.setTypes(types);
        applyStats(pokemon, pokeData);
    }

    private void applyStats(Pokemon pokemon, PokeApiPokemonResponse pokeData) {
        for (var statSlot : pokeData.stats()) {
            switch (statSlot.stat().name()) {
                case "hp" -> pokemon.setBaseHp(statSlot.baseStat());
                case "attack" -> pokemon.setBaseAttack(statSlot.baseStat());
                case "defense" -> pokemon.setBaseDefense(statSlot.baseStat());
                case "special-attack" -> pokemon.setBaseSpecialAttack(statSlot.baseStat());
                case "special-defense" -> pokemon.setBaseSpecialDefense(statSlot.baseStat());
                case "speed" -> pokemon.setBaseSpeed(statSlot.baseStat());
            }
        }
    }

    private TypeEntity findOrCreateType(String typeName) {
        return typeRepository.findByName(typeName)
                .orElseGet(() -> typeRepository.save(
                        TypeEntity.builder().name(typeName).build()
                ));
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}