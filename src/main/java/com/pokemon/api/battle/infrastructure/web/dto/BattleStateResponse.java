package com.pokemon.api.battle.infrastructure.web.dto;

import com.pokemon.api.battle.domain.entity.BattleStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BattleStateResponse(
        Long battleId,
        BattleStatus status,
        String attackerTrainer,
        String defenderTrainer,
        String attackerPokemon,
        String defenderPokemon,
        Integer attackerCurrentHp,
        Integer defenderCurrentHp,
        Integer attackerMaxHp,
        Integer defenderMaxHp,
        List<MoveOption> availableMoves,
        String winnerTrainer,
        String winnerPokemon,
        List<BattleTurnResponse> turns,
        LocalDateTime foughtAt
) {
    @Builder
    public record MoveOption(
            String name,
            String type,
            Integer power,
            Integer accuracy,
            String damageClass
    ) {
    }
}