package com.pokemon.api.shared.application.eventhandler;

import com.pokemon.api.achievement.application.usecase.BuildAchievementContextUseCase;
import com.pokemon.api.achievement.application.usecase.CheckAchievementsUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.event.PokemonCapturedEvent;
import com.pokemon.api.trainer.application.usecase.RegisterPokedexEntryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonCapturedEventHandler {

    private final RegisterPokedexEntryUseCase registerPokedexEntryUseCase;
    private final CheckAchievementsUseCase checkAchievementsUseCase;
    private final BuildAchievementContextUseCase buildAchievementContextUseCase;

    @EventListener
    public void handle(PokemonCapturedEvent event) {
        log.info("Event received: PokemonCapturedEvent — {} captured by {}",
                event.pokemon().getName(), event.trainer().getUsername());

        registerPokedexEntryUseCase.execute(event.pokemon(), ExecutionContext.empty());

        var context = buildAchievementContextUseCase.execute(
                new BuildAchievementContextUseCase.Input(event.trainer(), event.isLegendary()),
                ExecutionContext.empty()
        );
        checkAchievementsUseCase.execute(context, ExecutionContext.empty());
    }
}