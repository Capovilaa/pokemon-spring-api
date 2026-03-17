package com.pokemon.api.shared.infrastructure.messaging.consumer;

import com.pokemon.api.achievement.application.usecase.BuildAchievementContextUseCase;
import com.pokemon.api.achievement.application.usecase.CheckAchievementsUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.messaging.RabbitMQConfig;
import com.pokemon.api.shared.infrastructure.messaging.dto.BattleFinishedMessage;
import com.pokemon.api.trainer.application.usecase.UpdateTrainerStatsUseCase;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleFinishedConsumer {

    private final UpdateTrainerStatsUseCase updateTrainerStatsUseCase;
    private final CheckAchievementsUseCase checkAchievementsUseCase;
    private final BuildAchievementContextUseCase buildAchievementContextUseCase;
    private final TrainerRepository trainerRepository;

    @RabbitListener(queues = RabbitMQConfig.BATTLE_FINISHED_QUEUE)
    public void consume(BattleFinishedMessage message) {
        log.info("Consuming BattleFinishedMessage — {} defeated {}",
                message.winnerTrainerUsername(), message.loserTrainerUsername());

        var winner = trainerRepository.findById(message.winnerTrainerId())
                .orElseThrow(() -> new NotFoundException("Trainer", message.winnerTrainerId()));

        var loser = trainerRepository.findById(message.loserTrainerId())
                .orElseThrow(() -> new NotFoundException("Trainer", message.loserTrainerId()));

        updateTrainerStatsUseCase.execute(
                new UpdateTrainerStatsUseCase.Input(winner, loser),
                ExecutionContext.empty()
        );

        var winnerContext = buildAchievementContextUseCase.execute(
                new BuildAchievementContextUseCase.Input(winner, false),
                ExecutionContext.empty()
        );
        checkAchievementsUseCase.execute(winnerContext, ExecutionContext.empty());
    }
}