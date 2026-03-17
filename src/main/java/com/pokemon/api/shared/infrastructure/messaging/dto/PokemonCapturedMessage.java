package com.pokemon.api.shared.infrastructure.messaging.dto;

public record PokemonCapturedMessage(
        Long pokemonId,
        String pokemonName,
        Integer speciesId,
        Long trainerId,
        String trainerUsername,
        boolean isLegendary
) {
}