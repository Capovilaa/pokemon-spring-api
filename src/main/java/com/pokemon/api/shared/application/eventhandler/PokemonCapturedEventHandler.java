package com.pokemon.api.shared.application.eventhandler;

import com.pokemon.api.shared.domain.event.PokemonCapturedEvent;
import com.pokemon.api.shared.infrastructure.messaging.RabbitMQConfig;
import com.pokemon.api.shared.infrastructure.messaging.dto.PokemonCapturedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonCapturedEventHandler {

    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handle(PokemonCapturedEvent event) {
        log.info("Publishing PokemonCapturedEvent to RabbitMQ — {}",
                event.pokemon().getName());

        var message = new PokemonCapturedMessage(
                event.pokemon().getId(),
                event.pokemon().getName(),
                event.pokemon().getSpeciesId(),
                event.trainer().getId(),
                event.trainer().getUsername(),
                event.isLegendary()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.POKEMON_CAPTURED_KEY,
                message
        );
    }
}