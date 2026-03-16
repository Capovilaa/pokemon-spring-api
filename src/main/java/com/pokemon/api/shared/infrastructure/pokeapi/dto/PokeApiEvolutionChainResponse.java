package com.pokemon.api.shared.infrastructure.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiEvolutionChainResponse(
        Integer id,
        ChainLink chain
) {
    public record ChainLink(
            PokeApiPokemonResponse.PokeApiNamedResource species,
            @JsonProperty("evolves_to") List<ChainLink> evolvesTo,
            @JsonProperty("evolution_details") List<EvolutionDetail> evolutionDetails
    ) {
    }

    public record EvolutionDetail(
            @JsonProperty("min_level") Integer minLevel
    ) {
    }
}