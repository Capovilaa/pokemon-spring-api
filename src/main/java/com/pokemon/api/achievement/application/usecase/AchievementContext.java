package com.pokemon.api.achievement.application.usecase;

import com.pokemon.api.trainer.domain.entity.Trainer;
import lombok.Builder;

@Builder
public record AchievementContext(
        Trainer trainer,
        int totalPokemonsCaught,
        int totalWins,
        int totalEvolutions,
        int totalPokedexEntries,
        boolean justCaughtLegendary
) {
}