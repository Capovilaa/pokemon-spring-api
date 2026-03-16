package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.CreatePokemonRequest;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.shared.infrastructure.cache.CacheConfig;
import com.pokemon.api.shared.infrastructure.pokeapi.EvolutionService;
import com.pokemon.api.shared.infrastructure.pokeapi.PokeApiClient;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiPokemonResponse;
import com.pokemon.api.trainer.application.usecase.FindOrCreateTrainerUseCase;
import com.pokemon.api.trainer.application.usecase.RegisterPokedexEntryUseCase;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.type.domain.entity.TypeEntity;
import com.pokemon.api.type.domain.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatePokemonUseCase extends BaseUseCase<CreatePokemonRequest, PokemonResponse> {

    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;
    private final PokemonMapper pokemonMapper;
    private final FindOrCreateTrainerUseCase findOrCreateTrainerUseCase;
    private final PokeApiClient pokeApiClient;
    private final EvolutionService evolutionService;
    private final RegisterPokedexEntryUseCase registerPokedexEntryUseCase;

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.POKEMON_LIST_CACHE, key = "'all'")
    public PokemonResponse execute(CreatePokemonRequest input, ExecutionContext context) {
        PokeApiPokemonResponse pokeData = pokeApiClient.fetchPokemon(input.pokemonName());
        String canonicalName = capitalize(pokeData.name());

        if (pokemonRepository.existsByName(canonicalName)) {
            throw new ValidationException("Pokemon '" + canonicalName + "' already captured");
        }

        Set<TypeEntity> types = pokeData.types().stream()
                .map(slot -> findOrCreateType(slot.type().name()))
                .collect(Collectors.toSet());

        var stats = extractStats(pokeData);

        Trainer trainer = findOrCreateTrainerUseCase.execute(null, context);

        Pokemon pokemon = Pokemon.builder()
                .name(canonicalName)
                .level(input.level())
                .speciesId(pokeData.id())
                .baseHp(stats.hp())
                .baseAttack(stats.attack())
                .baseDefense(stats.defense())
                .baseSpecialAttack(stats.specialAttack())
                .baseSpecialDefense(stats.specialDefense())
                .baseSpeed(stats.speed())
                .spriteUrl(pokeData.sprites().frontDefault())
                .types(types)
                .trainer(trainer)
                .build();

        Pokemon saved = pokemonRepository.save(pokemon);
        registerPokedexEntryUseCase.execute(saved, context);

        String nextEvolution = evolutionService
                .getNextEvolution(saved.getName())
                .orElse(null);

        return pokemonMapper.toResponse(saved, nextEvolution);
    }

    private TypeEntity findOrCreateType(String typeName) {
        return typeRepository.findByName(typeName)
                .orElseGet(() -> typeRepository.save(
                        TypeEntity.builder()
                                .name(typeName)
                                .build()
                ));
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    private Stats extractStats(PokeApiPokemonResponse pokeData) {
        int hp = 0, attack = 0, defense = 0, spAtk = 0, spDef = 0, speed = 0;
        for (var statSlot : pokeData.stats()) {
            switch (statSlot.stat().name()) {
                case "hp" -> hp = statSlot.baseStat();
                case "attack" -> attack = statSlot.baseStat();
                case "defense" -> defense = statSlot.baseStat();
                case "special-attack" -> spAtk = statSlot.baseStat();
                case "special-defense" -> spDef = statSlot.baseStat();
                case "speed" -> speed = statSlot.baseStat();
            }
        }
        return new Stats(hp, attack, defense, spAtk, spDef, speed);
    }

    private record Stats(
            int hp, int attack, int defense,
            int specialAttack, int specialDefense, int speed
    ) {
    }
}