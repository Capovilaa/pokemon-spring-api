package com.pokemon.api.pokemon.infrastructure.web.dto;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PokemonMapper {

    @Mapping(target = "nextEvolution", source = "nextEvolution")
    PokemonResponse toResponse(Pokemon pokemon, String nextEvolution);

    @Mapping(target = "nextEvolution", ignore = true)
    PokemonResponse toResponse(Pokemon pokemon);
}