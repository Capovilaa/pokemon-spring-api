package com.pokemon.api.battle.domain.service;

import com.pokemon.api.battle.domain.entity.Move;
import com.pokemon.api.pokemon.domain.entity.Pokemon;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BattleDamageCalculator {

    private final Random random = new Random();

    public DamageResult calculate(Pokemon attacker, Pokemon defender, Move move) {
        if (!move.isUsable()) {
            return new DamageResult(0, 1.0, false);
        }

        int accuracyRoll = random.nextInt(100) + 1;
        if (move.accuracy() != null && accuracyRoll > move.accuracy()) {
            return new DamageResult(0, 1.0, false);
        }

        int attackStat = move.damageClass().equals("special")
                ? attacker.getBaseSpecialAttack()
                : attacker.getBaseAttack();
        int defenseStat = move.damageClass().equals("special")
                ? defender.getBaseSpecialDefense()
                : defender.getBaseDefense();

        double base = ((2.0 * attacker.getLevel() / 5 + 2)
                * move.power()
                * attackStat
                / defenseStat
                / 50.0) + 2;

        Set<String> attackerTypes = attacker.getTypes().stream()
                .map(t -> t.getName().toLowerCase())
                .collect(Collectors.toSet());
        double stab = attackerTypes.contains(move.type().toLowerCase()) ? 1.5 : 1.0;

        List<String> defenderTypes = defender.getTypes().stream()
                .map(t -> t.getName().toLowerCase())
                .toList();
        double effectiveness = TypeEffectiveness.getMultiplierAgainstTypes(move.type(), defenderTypes);

        boolean isCritical = random.nextInt(16) == 0;
        double critMultiplier = isCritical ? 1.5 : 1.0;

        double randomFactor = (random.nextInt(16) + 85) / 100.0;

        int damage = (int) Math.max(1, base * stab * effectiveness * critMultiplier * randomFactor);

        return new DamageResult(damage, effectiveness, isCritical);
    }

    public record DamageResult(int damage, double effectiveness, boolean isCritical) {
        public String effectivenessLabel() {
            if (effectiveness == 0.0) return "no_effect";
            if (effectiveness < 1.0) return "not_very_effective";
            if (effectiveness > 1.0) return "super_effective";
            return "normal";
        }
    }
}