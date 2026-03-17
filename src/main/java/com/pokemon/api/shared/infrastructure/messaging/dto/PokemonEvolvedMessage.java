package com.pokemon.api.shared.infrastructure.messaging.dto;

public record PokemonEvolvedMessage(
        Long pokemonId,
        String newName,
        String previousName,
        Long trainerId,
        String trainerUsername
) {
}