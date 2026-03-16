package com.pokemon.api.battle.infrastructure.web;

import com.pokemon.api.battle.application.usecase.StartBattleUseCase;
import com.pokemon.api.battle.infrastructure.web.dto.BattleResponse;
import com.pokemon.api.battle.infrastructure.web.dto.StartBattleRequest;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/battles")
@RequiredArgsConstructor
public class BattleController {

    private final StartBattleUseCase startBattleUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<BattleResponse> startBattle(@Valid @RequestBody StartBattleRequest request) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(startBattleUseCase.execute(request, context));
    }
}