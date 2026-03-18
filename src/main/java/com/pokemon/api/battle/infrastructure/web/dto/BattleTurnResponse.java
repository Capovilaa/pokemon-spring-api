package com.pokemon.api.battle.infrastructure.web.dto;

import lombok.Builder;

@Builder
public record BattleTurnResponse(
        Integer turn,
        String attackerPokemon,
        String defenderPokemon,
        String moveName,
        String moveType,
        Integer damage,
        Integer defenderHpLeft,
        Boolean isCritical,
        String effectiveness
) {
}