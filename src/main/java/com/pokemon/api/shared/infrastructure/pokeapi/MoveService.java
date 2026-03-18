package com.pokemon.api.shared.infrastructure.pokeapi;

import com.pokemon.api.battle.domain.entity.Move;
import com.pokemon.api.shared.infrastructure.pokeapi.dto.PokeApiMoveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveService {

    private final PokeApiClient pokeApiClient;

    private static final int MAX_MOVES = 4;

    public List<Move> getMovesForPokemon(String pokemonName) {
        var pokemonData = pokeApiClient.fetchPokemon(pokemonName);

        return pokemonData.moves().stream()
                .limit(MAX_MOVES)
                .map(slot -> fetchMove(slot.move().name()))
                .filter(Objects::nonNull)
                .filter(move -> move.power() != null && move.power() > 0)
                .limit(MAX_MOVES)
                .toList();
    }

    private Move fetchMove(String moveName) {
        try {
            PokeApiMoveResponse response = pokeApiClient.fetchMove(moveName);
            if (response == null) return null;

            return new Move(
                    response.name(),
                    response.type() != null ? response.type().name() : "normal",
                    response.power(),
                    response.accuracy() != null ? response.accuracy() : 100,
                    response.damageClass() != null ? response.damageClass().name() : "physical"
            );
        } catch (Exception e) {
            log.warn("Could not fetch move '{}': {}", moveName, e.getMessage());
            return null;
        }
    }
}