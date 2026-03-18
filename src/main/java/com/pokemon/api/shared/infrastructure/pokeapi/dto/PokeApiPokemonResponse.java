package com.pokemon.api.shared.infrastructure.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokeApiPokemonResponse(
        Integer id,
        String name,
        List<PokeApiTypeSlot> types,
        List<PokeApiStatSlot> stats,
        PokeApiSprites sprites,
        List<PokeApiMoveSlot> moves
) {
    public record PokeApiTypeSlot(
            Integer slot,
            PokeApiNamedResource type
    ) {
    }

    public record PokeApiStatSlot(
            @JsonProperty("base_stat") Integer baseStat,
            PokeApiNamedResource stat
    ) {
    }

    public record PokeApiSprites(
            @JsonProperty("front_default") String frontDefault
    ) {
    }

    public record PokeApiNamedResource(
            String name,
            String url
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokeApiMoveSlot(
            PokeApiNamedResource move
    ) {
    }
}