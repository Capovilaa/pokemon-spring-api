package com.pokemon.api.shared.application.eventhandler;

import com.pokemon.api.achievement.application.usecase.BuildAchievementContextUseCase;
import com.pokemon.api.achievement.application.usecase.CheckAchievementsUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.event.PokemonEvolvedEvent;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonEvolvedEventHandler {

    private final CheckAchievementsUseCase checkAchievementsUseCase;
    private final BuildAchievementContextUseCase buildAchievementContextUseCase;
    private final TrainerRepository trainerRepository;

    @EventListener
    public void handle(PokemonEvolvedEvent event) {
        log.info("Event received: PokemonEvolvedEvent — {} evolved from {}",
                event.pokemon().getName(), event.previousName());

        var trainer = event.trainer();
        trainer.setTotalEvolutions(trainer.getTotalEvolutions() + 1);
        trainerRepository.save(trainer);

        var context = buildAchievementContextUseCase.execute(
                new BuildAchievementContextUseCase.Input(trainer, false),
                ExecutionContext.empty()
        );
        checkAchievementsUseCase.execute(context, ExecutionContext.empty());
    }
}