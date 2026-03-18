package com.pokemon.api.battle.infrastructure.web;

import com.pokemon.api.battle.application.usecase.ExecuteTurnUseCase;
import com.pokemon.api.battle.application.usecase.StartBattleUseCase;
import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.battle.domain.entity.BattleStatus;
import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.battle.infrastructure.web.dto.BattleStateResponse;
import com.pokemon.api.battle.infrastructure.web.dto.BattleTurnResponse;
import com.pokemon.api.battle.infrastructure.web.dto.ExecuteTurnRequest;
import com.pokemon.api.battle.infrastructure.web.dto.StartBattleRequest;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.pokeapi.MoveService;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/battles")
@RequiredArgsConstructor
public class BattleController {

    private final StartBattleUseCase startBattleUseCase;
    private final ExecuteTurnUseCase executeTurnUseCase;
    private final BattleRepository battleRepository;
    private final MoveService moveService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<BattleStateResponse> startBattle(
            @Valid @RequestBody StartBattleRequest request) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(startBattleUseCase.execute(request, context));
    }

    @PostMapping("/{battleId}/turn")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<BattleStateResponse> executeTurn(
            @PathVariable Long battleId,
            @Valid @RequestBody ExecuteTurnRequest request) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(executeTurnUseCase.execute(
                new ExecuteTurnUseCase.Input(battleId, request), context));
    }

    @GetMapping("/{battleId}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<BattleStateResponse> getBattle(@PathVariable Long battleId) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());

        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new NotFoundException("Battle", battleId));

        List<com.pokemon.api.battle.domain.entity.Move> attackerMoves =
                battle.getStatus() == BattleStatus.IN_PROGRESS
                        ? moveService.getMovesForPokemon(battle.getAttackerPokemon().getName())
                        : List.of();

        List<BattleStateResponse.MoveOption> moveOptions = attackerMoves.stream()
                .map(m -> BattleStateResponse.MoveOption.builder()
                        .name(m.name())
                        .type(m.type())
                        .power(m.power())
                        .accuracy(m.accuracy())
                        .damageClass(m.damageClass())
                        .build())
                .toList();

        int attackerMaxHp = startBattleUseCase.calculateHp(battle.getAttackerPokemon());
        int defenderMaxHp = startBattleUseCase.calculateHp(battle.getDefenderPokemon());

        List<BattleTurnResponse> turnResponses = battle.getTurns().stream()
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

        return ResponseEntity.ok(BattleStateResponse.builder()
                .battleId(battle.getId())
                .status(battle.getStatus())
                .attackerTrainer(battle.getAttackerTrainer().getUsername())
                .defenderTrainer(battle.getDefenderTrainer().getUsername())
                .attackerPokemon(battle.getAttackerPokemon().getName())
                .defenderPokemon(battle.getDefenderPokemon().getName())
                .attackerCurrentHp(battle.getAttackerCurrentHp())
                .defenderCurrentHp(battle.getDefenderCurrentHp())
                .attackerMaxHp(attackerMaxHp)
                .defenderMaxHp(defenderMaxHp)
                .availableMoves(moveOptions)
                .winnerTrainer(battle.getWinnerTrainer() != null
                        ? battle.getWinnerTrainer().getUsername() : null)
                .winnerPokemon(battle.getWinnerPokemon() != null
                        ? battle.getWinnerPokemon().getName() : null)
                .turns(turnResponses)
                .foughtAt(battle.getFoughtAt())
                .build());
    }
}