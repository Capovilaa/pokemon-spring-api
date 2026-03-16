package com.pokemon.api.shared.infrastructure.pokeapi;

import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiEvolutionChainResponse;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiEvolutionChainResponse.ChainLink;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiSpeciesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvolutionService {

    private final PokeApiClient pokeApiClient;

    public Optional<String> getEvolution(String currentSpeciesName, int newLevel) {
        try {
            PokeApiSpeciesResponse species = pokeApiClient.fetchSpecies(currentSpeciesName);
            Integer chainId = extractChainId(species.evolutionChain().url());
            PokeApiEvolutionChainResponse chain = pokeApiClient.fetchEvolutionChain(chainId);
            return findNextEvolution(chain.chain(), currentSpeciesName.toLowerCase(), newLevel);
        } catch (Exception e) {
            log.warn("Could not fetch evolution for '{}': {}", currentSpeciesName, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getNextEvolution(String currentSpeciesName) {
        try {
            PokeApiSpeciesResponse species = pokeApiClient.fetchSpecies(currentSpeciesName);
            Integer chainId = extractChainId(species.evolutionChain().url());
            PokeApiEvolutionChainResponse chain = pokeApiClient.fetchEvolutionChain(chainId);
            return findNextEvolutionName(chain.chain(), currentSpeciesName.toLowerCase());
        } catch (Exception e) {
            log.warn("Could not fetch next evolution for '{}': {}", currentSpeciesName, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> findNextEvolution(ChainLink link, String currentName, int level) {
        if (link.species().name().equals(currentName)) {
            if (link.evolvesTo() == null || link.evolvesTo().isEmpty()) {
                return Optional.empty();
            }
            for (ChainLink next : link.evolvesTo()) {
                Integer minLevel = getMinLevel(next);
                if (minLevel != null && level >= minLevel) {
                    return Optional.of(next.species().name());
                }
            }
            return Optional.empty();
        }

        if (link.evolvesTo() != null) {
            for (ChainLink next : link.evolvesTo()) {
                Optional<String> result = findNextEvolution(next, currentName, level);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    private Optional<String> findNextEvolutionName(ChainLink link, String currentName) {
        if (link.species().name().equals(currentName)) {
            if (link.evolvesTo() == null || link.evolvesTo().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(link.evolvesTo().get(0).species().name());
        }

        if (link.evolvesTo() != null) {
            for (ChainLink next : link.evolvesTo()) {
                Optional<String> result = findNextEvolutionName(next, currentName);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    private Integer getMinLevel(ChainLink link) {
        if (link.evolutionDetails() == null || link.evolutionDetails().isEmpty()) {
            return null;
        }
        return link.evolutionDetails().get(0).minLevel();
    }

    private Integer extractChainId(String url) {
        String[] parts = url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}