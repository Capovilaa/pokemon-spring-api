package com.pokemon.api.battle.infrastructure.web.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BattleResponse(
        Long battleId,
        String winnerTrainer,
        String loserTrainer,
        String winnerPokemon,
        String loserPokemon,
        Integer totalTurns,
        List<BattleTurnResponse> turns,
        LocalDateTime foughtAt
) {
}