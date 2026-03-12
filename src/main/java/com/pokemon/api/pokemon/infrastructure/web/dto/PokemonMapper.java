package com.pokemon.api.pokemon.infrastructure.web.dto;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.trainer.infrastructure.web.dto.TrainerMapper;
import com.pokemon.api.type.infrastructure.web.dto.TypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {TypeMapper.class, TrainerMapper.class}
)
public interface PokemonMapper {

    PokemonResponse toResponse(Pokemon pokemon);
}