package com.pokemon.api.shared.infrastructure.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiPokemonResponse(
        Integer id,
        String name,
        List<PokeApiTypeSlot> types,
        List<PokeApiStatSlot> stats,
        PokeApiSprites sprites
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
}