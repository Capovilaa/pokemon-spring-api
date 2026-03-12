package com.pokemon.api.type.infraestructure.web.dto;

import com.pokemon.api.type.domain.entity.TypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TypeMapper {

    TypeResponse toResponse(TypeEntity type);
}