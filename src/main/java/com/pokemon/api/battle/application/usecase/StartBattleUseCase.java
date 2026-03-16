package com.pokemon.api.battle.application.usecase;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.battle.domain.entity.BattleTurn;
import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.battle.infrastructure.web.dto.BattleResponse;
import com.pokemon.api.battle.infrastructure.web.dto.BattleTurnResponse;
import com.pokemon.api.battle.infrastructure.web.dto.StartBattleRequest;
import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.event.BattleFinishedEvent;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartBattleUseCase extends BaseUseCase<StartBattleRequest, BattleResponse> {

    private final BattleRepository battleRepository;
    private final PokemonRepository pokemonRepository;
    private final TrainerRepository trainerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Random random = new Random();

    @Override
    @Transactional
    public BattleResponse execute(StartBattleRequest input, ExecutionContext context) {
        Trainer attackerTrainer = trainerRepository
                .findByKeycloakId(context.user().id())
                .orElseThrow(() -> new NotFoundException("Trainer", context.user().id()));

        Pokemon defenderPokemon = pokemonRepository
                .findById(input.defenderPokemonId())
                .orElseThrow(() -> new NotFoundException("Pokemon", input.defenderPokemonId()));

        Pokemon attackerPokemon = pokemonRepository
                .findByTrainer(attackerTrainer)
                .stream()
                .filter(p -> !p.getId().equals(input.defenderPokemonId()))
                .max((a, b) -> Integer.compare(a.getBaseAttack(), b.getBaseAttack()))
                .orElseThrow(() -> new ValidationException("Attacker trainer has no pokemon to battle with"));

        Trainer defenderTrainer = defenderPokemon.getTrainer();

        if (attackerTrainer.getId().equals(defenderTrainer.getId())) {
            throw new ValidationException("You cannot battle against your own pokemon");
        }

        int attackerHp = calculateHp(attackerPokemon);
        int defenderHp = calculateHp(defenderPokemon);

        log.info("Battle starting: {} (HP:{}) vs {} (HP:{})",
                attackerPokemon.getName(), attackerHp,
                defenderPokemon.getName(), defenderHp);

        boolean attackerGoesFirst = attackerPokemon.getBaseSpeed() >= defenderPokemon.getBaseSpeed();

        List<BattleTurn> turns = new ArrayList<>();
        int turnNumber = 1;

        while (attackerHp > 0 && defenderHp > 0) {
            if (attackerGoesFirst) {
                int damage = calculateDamage(attackerPokemon, defenderPokemon);
                defenderHp = Math.max(0, defenderHp - damage);
                turns.add(buildTurn(null, turnNumber++, attackerPokemon, defenderPokemon, damage, defenderHp));

                if (defenderHp <= 0) break;

                damage = calculateDamage(defenderPokemon, attackerPokemon);
                attackerHp = Math.max(0, attackerHp - damage);
                turns.add(buildTurn(null, turnNumber++, defenderPokemon, attackerPokemon, damage, attackerHp));
            } else {
                int damage = calculateDamage(defenderPokemon, attackerPokemon);
                attackerHp = Math.max(0, attackerHp - damage);
                turns.add(buildTurn(null, turnNumber++, defenderPokemon, attackerPokemon, damage, attackerHp));

                if (attackerHp <= 0) break;

                damage = calculateDamage(attackerPokemon, defenderPokemon);
                defenderHp = Math.max(0, defenderHp - damage);
                turns.add(buildTurn(null, turnNumber++, attackerPokemon, defenderPokemon, damage, defenderHp));
            }
        }

        boolean attackerWon = attackerHp > 0;
        Trainer winnerTrainer = attackerWon ? attackerTrainer : defenderTrainer;
        Trainer loserTrainer = attackerWon ? defenderTrainer : attackerTrainer;
        Pokemon winnerPokemon = attackerWon ? attackerPokemon : defenderPokemon;
        Pokemon loserPokemon = attackerWon ? defenderPokemon : attackerPokemon;

        log.info("Battle ended: {} won with {} after {} turns",
                winnerTrainer.getUsername(), winnerPokemon.getName(), turns.size());

        Battle battle = Battle.builder()
                .attackerTrainer(attackerTrainer)
                .defenderTrainer(defenderTrainer)
                .attackerPokemon(attackerPokemon)
                .defenderPokemon(defenderPokemon)
                .winnerTrainer(winnerTrainer)
                .winnerPokemon(winnerPokemon)
                .totalTurns(turns.size())
                .build();

        Battle saved = battleRepository.save(battle);

        turns.forEach(t -> t.setBattle(saved));
        saved.getTurns().addAll(turns);
        Battle finalBattle = battleRepository.save(saved);

        List<BattleTurnResponse> turnResponses = finalBattle.getTurns().stream()
                .map(t -> BattleTurnResponse.builder()
                        .turn(t.getTurnNumber())
                        .attackerPokemon(t.getAttackerPokemon().getName())
                        .defenderPokemon(t.getDefenderPokemon().getName())
                        .damage(t.getDamage())
                        .defenderHpLeft(t.getDefenderHpLeft())
                        .build())
                .toList();

        eventPublisher.publishEvent(
                new BattleFinishedEvent(finalBattle, winnerTrainer, loserTrainer)
        );

        return BattleResponse.builder()
                .battleId(finalBattle.getId())
                .winnerTrainer(winnerTrainer.getUsername())
                .loserTrainer(loserTrainer.getUsername())
                .winnerPokemon(winnerPokemon.getName())
                .loserPokemon(loserPokemon.getName())
                .totalTurns(finalBattle.getTurns().size())
                .turns(turnResponses)
                .foughtAt(finalBattle.getFoughtAt())
                .build();
    }

    private int calculateHp(Pokemon pokemon) {
        return ((2 * pokemon.getBaseHp() * pokemon.getLevel()) / 100) + pokemon.getLevel() + 10;
    }

    private int calculateDamage(Pokemon attacker, Pokemon defender) {
        int base = (int) ((double) attacker.getBaseAttack() / defender.getBaseDefense() * 10);
        return Math.max(1, base + random.nextInt(5) + 1);
    }

    private BattleTurn buildTurn(Battle battle, int number, Pokemon attacker, Pokemon defender,
                                 int damage, int defenderHpLeft) {
        return BattleTurn.builder()
                .battle(battle)
                .turnNumber(number)
                .attackerPokemon(attacker)
                .defenderPokemon(defender)
                .damage(damage)
                .defenderHpLeft(defenderHpLeft)
                .build();
    }
}