package com.pokemon.api.shared.infrastructure.messaging.dto;

public record BattleFinishedMessage(
        Long battleId,
        Long winnerTrainerId,
        String winnerTrainerUsername,
        Long loserTrainerId,
        String loserTrainerUsername,
        String winnerPokemonName,
        String loserPokemonName,
        Integer totalTurns
) {
}