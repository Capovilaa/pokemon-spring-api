package com.pokemon.api.battle.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record StartBattleRequest(
        @NotNull Long defenderPokemonId
) {
}