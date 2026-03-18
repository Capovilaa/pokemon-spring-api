package com.pokemon.api.shared.infrastructure.pokeapi;

import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.cache.CacheConfig;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiEvolutionChainResponse;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiMoveResponse;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiPokemonResponse;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiSpeciesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class PokeApiClient {

    private final RestClient restClient;

    public PokeApiClient(@Value("${pokeapi.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Cacheable(value = CacheConfig.POKEAPI_CACHE, key = "'pokemon-' + #name.toLowerCase()")
    public PokeApiPokemonResponse fetchPokemon(String name) {
        log.info("Fetching pokemon '{}' from PokeAPI", name);
        try {
            return restClient.get()
                    .uri("/pokemon/{name}", name.toLowerCase())
                    .retrieve()
                    .body(PokeApiPokemonResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Pokemon species '" + name + "' not found in PokeAPI");
        }
    }

    @Cacheable(value = CacheConfig.POKEAPI_CACHE, key = "'species-' + #name.toLowerCase()")
    public PokeApiSpeciesResponse fetchSpecies(String name) {
        log.info("Fetching species '{}' from PokeAPI", name);
        try {
            return restClient.get()
                    .uri("/pokemon-species/{name}", name.toLowerCase())
                    .retrieve()
                    .body(PokeApiSpeciesResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Species '" + name + "' not found in PokeAPI");
        }
    }

    @Cacheable(value = CacheConfig.POKEAPI_CACHE, key = "'evolution-chain-' + #id")
    public PokeApiEvolutionChainResponse fetchEvolutionChain(Integer id) {
        log.info("Fetching evolution chain '{}' from PokeAPI", id);
        return restClient.get()
                .uri("/evolution-chain/{id}", id)
                .retrieve()
                .body(PokeApiEvolutionChainResponse.class);
    }

    @Cacheable(value = CacheConfig.POKEAPI_CACHE, key = "'move-' + #name.toLowerCase()")
    public PokeApiMoveResponse fetchMove(String name) {
        log.info("Fetching move '{}' from PokeAPI", name);
        try {
            return restClient.get()
                    .uri("/move/{name}", name.toLowerCase())
                    .retrieve()
                    .body(PokeApiMoveResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Move '{}' not found in PokeAPI", name);
            return null;
        }
    }
}