package com.pokemon.api.trainer.infrastructure.web.dto;

import com.pokemon.api.trainer.domain.entity.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    TrainerResponse toResponse(Trainer trainer);

    @Mapping(target = "winRate", expression = "java(calculateWinRate(trainer))")
    TrainerStatsResponse toStatsResponse(Trainer trainer);

    default double calculateWinRate(Trainer trainer) {
        if (trainer.getTotalBattles() == 0) return 0.0;
        return Math.round((trainer.getWins() * 100.0 / trainer.getTotalBattles()) * 10.0) / 10.0;
    }
}