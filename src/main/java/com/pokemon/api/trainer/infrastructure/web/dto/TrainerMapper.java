package com.pokemon.api.trainer.infrastructure.web.dto;

import com.pokemon.api.trainer.domain.entity.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {

    TrainerResponse toResponse(Trainer trainer);
}