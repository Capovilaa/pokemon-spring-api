package com.pokemon.api.battle.domain.service;

import com.pokemon.api.battle.domain.entity.Move;
import com.pokemon.api.pokemon.domain.entity.Pokemon;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class BattleAiStrategy {

    private final Random random = new Random();

    public Move chooseMove(Pokemon aiPokemon, Pokemon opponent, List<Move> availableMoves) {
        List<Move> usableMoves = availableMoves.stream()
                .filter(Move::isUsable)
                .collect(Collectors.toList());

        if (usableMoves.isEmpty()) {
            return availableMoves.get(0);
        }

        List<String> opponentTypes = opponent.getTypes().stream()
                .map(t -> t.getName().toLowerCase())
                .toList();

        List<WeightedMove> weighted = usableMoves.stream()
                .map(move -> {
                    double effectiveness = TypeEffectiveness
                            .getMultiplierAgainstTypes(move.type(), opponentTypes);

                    if (effectiveness == 0.0) return new WeightedMove(move, 0);

                    int weight = effectiveness > 1.0 ? 3 : effectiveness < 1.0 ? 1 : 2;

                    return new WeightedMove(move, weight);
                })
                .filter(wm -> wm.weight() > 0)
                .collect(Collectors.toList());

        if (weighted.isEmpty()) {
            return usableMoves.get(0);
        }

        int totalWeight = weighted.stream().mapToInt(WeightedMove::weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;

        for (WeightedMove wm : weighted) {
            cumulative += wm.weight();
            if (roll < cumulative) {
                return wm.move();
            }
        }

        return weighted.get(0).move();
    }

    private record WeightedMove(Move move, int weight) {
    }
}