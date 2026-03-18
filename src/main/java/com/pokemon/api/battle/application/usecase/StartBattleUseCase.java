package com.pokemon.api.battle.application.usecase;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.battle.domain.entity.BattleStatus;
import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.battle.infrastructure.web.dto.BattleStateResponse;
import com.pokemon.api.battle.infrastructure.web.dto.StartBattleRequest;
import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.shared.infrastructure.pokeapi.MoveService;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartBattleUseCase extends BaseUseCase<StartBattleRequest, BattleStateResponse> {

    private final BattleRepository battleRepository;
    private final PokemonRepository pokemonRepository;
    private final TrainerRepository trainerRepository;
    private final MoveService moveService;

    @Override
    @Transactional
    public BattleStateResponse execute(StartBattleRequest input, ExecutionContext context) {
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

        int attackerMaxHp = calculateHp(attackerPokemon);
        int defenderMaxHp = calculateHp(defenderPokemon);

        Battle battle = Battle.builder()
                .attackerTrainer(attackerTrainer)
                .defenderTrainer(defenderTrainer)
                .attackerPokemon(attackerPokemon)
                .defenderPokemon(defenderPokemon)
                .status(BattleStatus.IN_PROGRESS)
                .attackerCurrentHp(attackerMaxHp)
                .defenderCurrentHp(defenderMaxHp)
                .totalTurns(0)
                .build();

        Battle saved = battleRepository.save(battle);

        var attackerMoves = moveService.getMovesForPokemon(attackerPokemon.getName());

        log.info("Battle {} started: {} (HP:{}) vs {} (HP:{})",
                saved.getId(),
                attackerPokemon.getName(), attackerMaxHp,
                defenderPokemon.getName(), defenderMaxHp);

        List<BattleStateResponse.MoveOption> moveOptions = attackerMoves.stream()
                .map(m -> BattleStateResponse.MoveOption.builder()
                        .name(m.name())
                        .type(m.type())
                        .power(m.power())
                        .accuracy(m.accuracy())
                        .damageClass(m.damageClass())
                        .build())
                .toList();

        return BattleStateResponse.builder()
                .battleId(saved.getId())
                .status(saved.getStatus())
                .attackerTrainer(attackerTrainer.getUsername())
                .defenderTrainer(defenderTrainer.getUsername())
                .attackerPokemon(attackerPokemon.getName())
                .defenderPokemon(defenderPokemon.getName())
                .attackerCurrentHp(attackerMaxHp)
                .defenderCurrentHp(defenderMaxHp)
                .attackerMaxHp(attackerMaxHp)
                .defenderMaxHp(defenderMaxHp)
                .availableMoves(moveOptions)
                .turns(List.of())
                .foughtAt(saved.getFoughtAt())
                .build();
    }

    public int calculateHp(Pokemon pokemon) {
        return ((2 * pokemon.getBaseHp() * pokemon.getLevel()) / 100) + pokemon.getLevel() + 10;
    }
}