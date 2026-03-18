package com.pokemon.api.shared.infrastructure.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokeApiMoveResponse(
        String name,
        Integer power,
        Integer accuracy,
        @JsonProperty("damage_class") DamageClass damageClass,
        Type type
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DamageClass(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Type(String name) {
    }
}