package com.pokemon.api.shared.infrastructure.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokeApiSpeciesResponse(
        Integer id,
        String name,
        @JsonProperty("evolution_chain") EvolutionChainRef evolutionChain
) {
    public record EvolutionChainRef(String url) {
    }
}