package com.pokemon.api.trainer.infrastructure.web;

import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import com.pokemon.api.trainer.application.usecase.GetMyStatsUseCase;
import com.pokemon.api.trainer.application.usecase.GetPokedexUseCase;
import com.pokemon.api.trainer.application.usecase.GetRankingUseCase;
import com.pokemon.api.trainer.infrastructure.web.dto.PokedexResponse;
import com.pokemon.api.trainer.infrastructure.web.dto.TrainerStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final GetPokedexUseCase getPokedexUseCase;
    private final GetRankingUseCase getRankingUseCase;
    private final GetMyStatsUseCase getMyStatsUseCase;

    @GetMapping("/me/pokedex")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<PokedexResponse> getPokedex() {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(getPokedexUseCase.execute(null, context));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<TrainerStatsResponse>> getRanking() {
        return ResponseEntity.ok(getRankingUseCase.execute(null, ExecutionContext.empty()));
    }

    @GetMapping("/me/stats")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<TrainerStatsResponse> getMyStats() {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(getMyStatsUseCase.execute(null, context));
    }
}