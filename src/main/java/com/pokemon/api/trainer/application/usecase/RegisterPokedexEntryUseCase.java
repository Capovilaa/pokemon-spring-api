package com.pokemon.api.trainer.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.trainer.domain.entity.PokedexEntry;
import com.pokemon.api.trainer.domain.repository.PokedexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterPokedexEntryUseCase extends BaseUseCase<Pokemon, Void> {

    private final PokedexRepository pokedexRepository;

    @Override
    public Void execute(Pokemon pokemon, ExecutionContext context) {
        if (pokemon.getTrainer() == null || pokemon.getSpeciesId() == null) {
            return null;
        }

        boolean alreadyRegistered = pokedexRepository
                .existsByTrainerAndSpeciesId(pokemon.getTrainer(), pokemon.getSpeciesId());

        if (alreadyRegistered) {
            log.info("Species '{}' already in pokedex for trainer '{}'",
                    pokemon.getName(), pokemon.getTrainer().getUsername());
            return null;
        }

        PokedexEntry entry = PokedexEntry.builder()
                .trainer(pokemon.getTrainer())
                .speciesId(pokemon.getSpeciesId())
                .speciesName(pokemon.getName().toLowerCase())
                .spriteUrl(pokemon.getSpriteUrl())
                .build();

        pokedexRepository.save(entry);
        log.info("Registered '{}' in pokedex for trainer '{}'",
                pokemon.getName(), pokemon.getTrainer().getUsername());

        return null;
    }
}