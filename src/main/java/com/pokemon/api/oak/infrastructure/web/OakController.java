package com.pokemon.api.oak.infrastructure.web;

import com.pokemon.api.oak.application.usecase.AnalyzeBattleUseCase;
import com.pokemon.api.oak.application.usecase.AskOakUseCase;
import com.pokemon.api.oak.application.usecase.PokedexAdviceUseCase;
import com.pokemon.api.oak.infrastructure.web.dto.OakQuestionRequest;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/oak")
@RequiredArgsConstructor
public class OakController {

    private final AskOakUseCase askOakUseCase;
    private final AnalyzeBattleUseCase analyzeBattleUseCase;
    private final PokedexAdviceUseCase pokedexAdviceUseCase;

    @PostMapping("/ask")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> ask(@Valid @RequestBody OakQuestionRequest request) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(askOakUseCase.execute(request.question(), context));
    }

    @GetMapping("/analyze-battle/{battleId}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> analyzeBattle(@PathVariable Long battleId) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(analyzeBattleUseCase.execute(battleId, context));
    }

    @GetMapping("/pokedex-advice")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> pokedexAdvice() {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(pokedexAdviceUseCase.execute(null, context));
    }
}