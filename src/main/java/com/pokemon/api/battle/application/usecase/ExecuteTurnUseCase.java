package com.pokemon.api.battle.application.usecase;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.battle.domain.entity.BattleStatus;
import com.pokemon.api.battle.domain.entity.BattleTurn;
import com.pokemon.api.battle.domain.entity.Move;
import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.battle.domain.service.BattleAiStrategy;
import com.pokemon.api.battle.domain.service.BattleDamageCalculator;
import com.pokemon.api.battle.infrastructure.web.dto.BattleStateResponse;
import com.pokemon.api.battle.infrastructure.web.dto.BattleTurnResponse;
import com.pokemon.api.battle.infrastructure.web.dto.ExecuteTurnRequest;
import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.event.BattleFinishedEvent;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.shared.infrastructure.pokeapi.MoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecuteTurnUseCase extends BaseUseCase<ExecuteTurnUseCase.Input, BattleStateResponse> {

    private final BattleRepository battleRepository;
    private final MoveService moveService;
    private final BattleDamageCalculator damageCalculator;
    private final BattleAiStrategy aiStrategy;
    private final StartBattleUseCase startBattleUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public record Input(Long battleId, ExecuteTurnRequest request) {
    }

    @Override
    @Transactional
    public BattleStateResponse execute(Input input, ExecutionContext context) {
        Battle battle = battleRepository.findById(input.battleId())
                .orElseThrow(() -> new NotFoundException("Battle", input.battleId()));

        if (battle.getStatus() == BattleStatus.FINISHED) {
            throw new ValidationException("Battle " + input.battleId() + " is already finished");
        }

        Pokemon attacker = battle.getAttackerPokemon();
        Pokemon defender = battle.getDefenderPokemon();

        var attackerMoves = moveService.getMovesForPokemon(attacker.getName());
        var defenderMoves = moveService.getMovesForPokemon(defender.getName());

        String chosenMoveName = input.request().moveName();
        Move attackerMove = attackerMoves.stream()
                .filter(m -> m.name().equalsIgnoreCase(chosenMoveName))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        "Move '" + chosenMoveName + "' is not available for " + attacker.getName()));

        Move defenderMove = aiStrategy.chooseMove(defender, attacker, defenderMoves);

        int attackerHp = battle.getAttackerCurrentHp();
        int defenderHp = battle.getDefenderCurrentHp();

        boolean attackerGoesFirst = attacker.getBaseSpeed() >= defender.getBaseSpeed();

        BattleTurn turn1 = null;
        BattleTurn turn2 = null;
        int turnNumber = battle.getTurns().size() + 1;

        if (attackerGoesFirst) {
            var dmg1 = damageCalculator.calculate(attacker, defender, attackerMove);
            defenderHp = Math.max(0, defenderHp - dmg1.damage());
            turn1 = buildTurn(battle, turnNumber++, attacker, defender,
                    attackerMove, dmg1.damage(), defenderHp, dmg1.isCritical(), dmg1.effectivenessLabel());

            log.info("Turn {}: {} used {} → {} damage to {} (HP: {}{})",
                    turnNumber - 1, attacker.getName(), attackerMove.name(),
                    dmg1.damage(), defender.getName(), defenderHp,
                    dmg1.isCritical() ? " CRITICAL!" : "");

            if (defenderHp > 0) {
                var dmg2 = damageCalculator.calculate(defender, attacker, defenderMove);
                attackerHp = Math.max(0, attackerHp - dmg2.damage());
                turn2 = buildTurn(battle, turnNumber++, defender, attacker,
                        defenderMove, dmg2.damage(), attackerHp, dmg2.isCritical(), dmg2.effectivenessLabel());

                log.info("Turn {}: {} used {} → {} damage to {} (HP: {}{})",
                        turnNumber - 1, defender.getName(), defenderMove.name(),
                        dmg2.damage(), attacker.getName(), attackerHp,
                        dmg2.isCritical() ? " CRITICAL!" : "");
            }
        } else {
            var dmg1 = damageCalculator.calculate(defender, attacker, defenderMove);
            attackerHp = Math.max(0, attackerHp - dmg1.damage());
            turn1 = buildTurn(battle, turnNumber++, defender, attacker,
                    defenderMove, dmg1.damage(), attackerHp, dmg1.isCritical(), dmg1.effectivenessLabel());

            log.info("Turn {}: {} used {} → {} damage to {} (HP: {}{})",
                    turnNumber - 1, defender.getName(), defenderMove.name(),
                    dmg1.damage(), attacker.getName(), attackerHp,
                    dmg1.isCritical() ? " CRITICAL!" : "");

            if (attackerHp > 0) {
                var dmg2 = damageCalculator.calculate(attacker, defender, attackerMove);
                defenderHp = Math.max(0, defenderHp - dmg2.damage());
                turn2 = buildTurn(battle, turnNumber++, attacker, defender,
                        attackerMove, dmg2.damage(), defenderHp, dmg2.isCritical(), dmg2.effectivenessLabel());

                log.info("Turn {}: {} used {} → {} damage to {} (HP: {}{})",
                        turnNumber - 1, attacker.getName(), attackerMove.name(),
                        dmg2.damage(), defender.getName(), defenderHp,
                        dmg2.isCritical() ? " CRITICAL!" : "");
            }
        }

        battle.getTurns().add(turn1);
        if (turn2 != null) battle.getTurns().add(turn2);

        battle.setAttackerCurrentHp(attackerHp);
        battle.setDefenderCurrentHp(defenderHp);
        battle.setTotalTurns(battle.getTurns().size());

        boolean attackerWon = defenderHp <= 0;
        boolean defenderWon = attackerHp <= 0;

        if (attackerWon || defenderWon) {
            var winner = attackerWon ? battle.getAttackerTrainer() : battle.getDefenderTrainer();
            var loser = attackerWon ? battle.getDefenderTrainer() : battle.getAttackerTrainer();
            var winnerPokemon = attackerWon ? attacker : defender;

            battle.setStatus(BattleStatus.FINISHED);
            battle.setWinnerTrainer(winner);
            battle.setWinnerPokemon(winnerPokemon);

            log.info("Battle {} finished! {} won with {} after {} turns",
                    battle.getId(), winner.getUsername(),
                    winnerPokemon.getName(), battle.getTotalTurns());

            eventPublisher.publishEvent(new BattleFinishedEvent(battle, winner, loser));
        }

        Battle saved = battleRepository.save(battle);

        List<BattleStateResponse.MoveOption> moveOptions = saved.getStatus() == BattleStatus.IN_PROGRESS
                ? attackerMoves.stream()
                .map(m -> BattleStateResponse.MoveOption.builder()
                        .name(m.name())
                        .type(m.type())
                        .power(m.power())
                        .accuracy(m.accuracy())
                        .damageClass(m.damageClass())
                        .build())
                .toList()
                : List.of();

        int attackerMaxHp = startBattleUseCase.calculateHp(attacker);
        int defenderMaxHp = startBattleUseCase.calculateHp(defender);

        List<BattleTurnResponse> turnResponses = saved.getTurns().stream()
                .map(t -> BattleTurnResponse.builder()
                        .turn(t.getTurnNumber())
                        .attackerPokemon(t.getAttackerPokemon().getName())
                        .defenderPokemon(t.getDefenderPokemon().getName())
                        .moveName(t.getMoveName())
                        .moveType(t.getMoveType())
                        .damage(t.getDamage())
                        .defenderHpLeft(t.getDefenderHpLeft())
                        .isCritical(t.getIsCritical())
                        .effectiveness(t.getEffectiveness())
                        .build())
                .toList();

        return BattleStateResponse.builder()
                .battleId(saved.getId())
                .status(saved.getStatus())
                .attackerTrainer(battle.getAttackerTrainer().getUsername())
                .defenderTrainer(battle.getDefenderTrainer().getUsername())
                .attackerPokemon(attacker.getName())
                .defenderPokemon(defender.getName())
                .attackerCurrentHp(saved.getAttackerCurrentHp())
                .defenderCurrentHp(saved.getDefenderCurrentHp())
                .attackerMaxHp(attackerMaxHp)
                .defenderMaxHp(defenderMaxHp)
                .availableMoves(moveOptions)
                .winnerTrainer(saved.getWinnerTrainer() != null
                        ? saved.getWinnerTrainer().getUsername() : null)
                .winnerPokemon(saved.getWinnerPokemon() != null
                        ? saved.getWinnerPokemon().getName() : null)
                .turns(turnResponses)
                .foughtAt(saved.getFoughtAt())
                .build();
    }

    private BattleTurn buildTurn(Battle battle, int number, Pokemon attacker, Pokemon defender,
                                 Move move, int damage, int defenderHpLeft,
                                 boolean isCritical, String effectiveness) {
        return BattleTurn.builder()
                .battle(battle)
                .turnNumber(number)
                .attackerPokemon(attacker)
                .defenderPokemon(defender)
                .moveName(move.name())
                .moveType(move.type())
                .damage(damage)
                .defenderHpLeft(defenderHpLeft)
                .isCritical(isCritical)
                .effectiveness(effectiveness)
                .build();
    }
}