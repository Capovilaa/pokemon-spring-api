package com.pokemon.api.shared.domain.event;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.trainer.domain.entity.Trainer;

public record PokemonEvolvedEvent(
        Pokemon pokemon,
        Trainer trainer,
        String previousName
) {
}