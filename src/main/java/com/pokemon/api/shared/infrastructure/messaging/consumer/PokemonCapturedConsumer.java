package com.pokemon.api.shared.infrastructure.messaging.consumer;

import com.pokemon.api.achievement.application.usecase.BuildAchievementContextUseCase;
import com.pokemon.api.achievement.application.usecase.CheckAchievementsUseCase;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.shared.infrastructure.messaging.RabbitMQConfig;
import com.pokemon.api.shared.infrastructure.messaging.dto.PokemonCapturedMessage;
import com.pokemon.api.trainer.application.usecase.RegisterPokedexEntryUseCase;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonCapturedConsumer {

    private final RegisterPokedexEntryUseCase registerPokedexEntryUseCase;
    private final CheckAchievementsUseCase checkAchievementsUseCase;
    private final BuildAchievementContextUseCase buildAchievementContextUseCase;
    private final PokemonRepository pokemonRepository;
    private final TrainerRepository trainerRepository;

    @RabbitListener(queues = RabbitMQConfig.POKEMON_CAPTURED_QUEUE)
    public void consume(PokemonCapturedMessage message) {
        log.info("Consuming PokemonCapturedMessage — {} captured by {}",
                message.pokemonName(), message.trainerUsername());

        var pokemon = pokemonRepository.findById(message.pokemonId())
                .orElseThrow(() -> new NotFoundException("Pokemon", message.pokemonId()));

        var trainer = trainerRepository.findById(message.trainerId())
                .orElseThrow(() -> new NotFoundException("Trainer", message.trainerId()));

        registerPokedexEntryUseCase.execute(pokemon, ExecutionContext.empty());

        var context = buildAchievementContextUseCase.execute(
                new BuildAchievementContextUseCase.Input(trainer, message.isLegendary()),
                ExecutionContext.empty()
        );
        checkAchievementsUseCase.execute(context, ExecutionContext.empty());
    }
}