package com.pokemon.api.shared.infrastructure.messaging.consumer;

import com.pokemon.api.achievement.application.usecase.BuildAchievementContextUseCase;
import com.pokemon.api.achievement.application.usecase.CheckAchievementsUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.messaging.RabbitMQConfig;
import com.pokemon.api.shared.infrastructure.messaging.dto.PokemonEvolvedMessage;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonEvolvedConsumer {

    private final CheckAchievementsUseCase checkAchievementsUseCase;
    private final BuildAchievementContextUseCase buildAchievementContextUseCase;
    private final TrainerRepository trainerRepository;

    @RabbitListener(queues = RabbitMQConfig.POKEMON_EVOLVED_QUEUE)
    public void consume(PokemonEvolvedMessage message) {
        log.info("Consuming PokemonEvolvedMessage — {} evolved from {}",
                message.newName(), message.previousName());

        var trainer = trainerRepository.findById(message.trainerId())
                .orElseThrow(() -> new NotFoundException("Trainer", message.trainerId()));

        trainer.setTotalEvolutions(trainer.getTotalEvolutions() + 1);
        trainerRepository.save(trainer);

        var context = buildAchievementContextUseCase.execute(
                new BuildAchievementContextUseCase.Input(trainer, false),
                ExecutionContext.empty()
        );
        checkAchievementsUseCase.execute(context, ExecutionContext.empty());
    }
}