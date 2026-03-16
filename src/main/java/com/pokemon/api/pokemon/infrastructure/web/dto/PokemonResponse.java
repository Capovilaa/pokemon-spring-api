package com.pokemon.api.pokemon.infrastructure.web.dto;

import com.pokemon.api.trainer.infrastructure.web.dto.TrainerResponse;
import com.pokemon.api.type.infrastructure.web.dto.TypeResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record PokemonResponse(
        Long id,
        String name,
        Integer level,
        Integer speciesId,
        Integer baseHp,
        Integer baseAttack,
        Integer baseDefense,
        Integer baseSpecialAttack,
        Integer baseSpecialDefense,
        Integer baseSpeed,
        String spriteUrl,
        String nextEvolution,
        Set<TypeResponse> types,
        TrainerResponse trainer,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}