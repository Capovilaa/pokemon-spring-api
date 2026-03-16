package com.pokemon.api.shared.application.eventhandler;

import com.pokemon.api.achievement.application.usecase.BuildAchievementContextUseCase;
import com.pokemon.api.achievement.application.usecase.CheckAchievementsUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.event.BattleFinishedEvent;
import com.pokemon.api.trainer.application.usecase.UpdateTrainerStatsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleFinishedEventHandler {

    private final UpdateTrainerStatsUseCase updateTrainerStatsUseCase;
    private final CheckAchievementsUseCase checkAchievementsUseCase;
    private final BuildAchievementContextUseCase buildAchievementContextUseCase;

    @EventListener
    public void handle(BattleFinishedEvent event) {
        log.info("Event received: BattleFinishedEvent — {} defeated {}",
                event.winner().getUsername(), event.loser().getUsername());

        updateTrainerStatsUseCase.execute(
                new UpdateTrainerStatsUseCase.Input(event.winner(), event.loser()),
                ExecutionContext.empty()
        );

        var winnerContext = buildAchievementContextUseCase.execute(
                new BuildAchievementContextUseCase.Input(event.winner(), false),
                ExecutionContext.empty()
        );
        checkAchievementsUseCase.execute(winnerContext, ExecutionContext.empty());
    }
}