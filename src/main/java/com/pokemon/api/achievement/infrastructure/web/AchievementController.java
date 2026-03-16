package com.pokemon.api.achievement.infrastructure.web;

import com.pokemon.api.achievement.application.usecase.GetMyAchievementsUseCase;
import com.pokemon.api.achievement.infrastructure.web.dto.AchievementResponse;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final GetMyAchievementsUseCase getMyAchievementsUseCase;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<List<AchievementResponse>> getMyAchievements() {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(getMyAchievementsUseCase.execute(null, context));
    }
}