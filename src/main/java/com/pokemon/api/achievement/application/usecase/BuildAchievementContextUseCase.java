package com.pokemon.api.achievement.application.usecase;

import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.pokeapi.LegendarySpecies;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.PokedexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildAchievementContextUseCase extends BaseUseCase<BuildAchievementContextUseCase.Input, AchievementContext> {

    private final PokemonRepository pokemonRepository;
    private final PokedexRepository pokedexRepository;

    public record Input(Trainer trainer, boolean justCaughtLegendary) {
    }

    @Override
    public AchievementContext execute(Input input, ExecutionContext context) {
        Trainer trainer = input.trainer();

        int totalPokemonsCaught = pokemonRepository.findByTrainer(trainer).size();
        int totalPokedexEntries = (int) pokedexRepository.countByTrainer(trainer);

        return AchievementContext.builder()
                .trainer(trainer)
                .totalPokemonsCaught(totalPokemonsCaught)
                .totalWins(trainer.getWins())
                .totalEvolutions(trainer.getTotalEvolutions())
                .totalPokedexEntries(totalPokedexEntries)
                .justCaughtLegendary(input.justCaughtLegendary())
                .build();
    }
}