package com.pokemon.api.battle.infrastructure.web.dto;

import lombok.Builder;

@Builder
public record BattleTurnResponse(
        Integer turn,
        String attackerPokemon,
        String defenderPokemon,
        Integer damage,
        Integer defenderHpLeft
) {
}