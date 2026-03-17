package com.pokemon.api.shared.application.eventhandler;

import com.pokemon.api.shared.domain.event.BattleFinishedEvent;
import com.pokemon.api.shared.infrastructure.messaging.RabbitMQConfig;
import com.pokemon.api.shared.infrastructure.messaging.dto.BattleFinishedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleFinishedEventHandler {

    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handle(BattleFinishedEvent event) {
        log.info("Publishing BattleFinishedEvent to RabbitMQ — {} defeated {}",
                event.winner().getUsername(), event.loser().getUsername());

        var message = new BattleFinishedMessage(
                event.battle().getId(),
                event.winner().getId(),
                event.winner().getUsername(),
                event.loser().getId(),
                event.loser().getUsername(),
                event.battle().getWinnerPokemon().getName(),
                event.battle().getDefenderPokemon().getName(),
                event.battle().getTotalTurns()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.BATTLE_FINISHED_KEY,
                message
        );
    }
}