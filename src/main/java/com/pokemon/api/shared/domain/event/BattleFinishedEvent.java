package com.pokemon.api.shared.domain.event;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.trainer.domain.entity.Trainer;

public record BattleFinishedEvent(
        Battle battle,
        Trainer winner,
        Trainer loser
) {
}