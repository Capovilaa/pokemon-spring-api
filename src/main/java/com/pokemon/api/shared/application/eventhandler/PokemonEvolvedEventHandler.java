package com.pokemon.api.shared.application.eventhandler;

import com.pokemon.api.shared.domain.event.PokemonEvolvedEvent;
import com.pokemon.api.shared.infrastructure.messaging.RabbitMQConfig;
import com.pokemon.api.shared.infrastructure.messaging.dto.PokemonEvolvedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonEvolvedEventHandler {

    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handle(PokemonEvolvedEvent event) {
        log.info("Publishing PokemonEvolvedEvent to RabbitMQ — {} evolved from {}",
                event.pokemon().getName(), event.previousName());

        var message = new PokemonEvolvedMessage(
                event.pokemon().getId(),
                event.pokemon().getName(),
                event.previousName(),
                event.trainer().getId(),
                event.trainer().getUsername()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.POKEMON_EVOLVED_KEY,
                message
        );
    }
}