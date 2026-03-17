package com.pokemon.api.shared.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "pokemon.events";
    public static final String POKEMON_CAPTURED_QUEUE = "pokemon.captured";
    public static final String POKEMON_EVOLVED_QUEUE = "pokemon.evolved";
    public static final String BATTLE_FINISHED_QUEUE = "battle.finished";
    public static final String POKEMON_CAPTURED_KEY = "pokemon.captured";
    public static final String POKEMON_EVOLVED_KEY = "pokemon.evolved";
    public static final String BATTLE_FINISHED_KEY = "battle.finished";

    @Bean
    public TopicExchange pokemonExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue pokemonCapturedQueue() {
        return QueueBuilder.durable(POKEMON_CAPTURED_QUEUE).build();
    }

    @Bean
    public Queue pokemonEvolvedQueue() {
        return QueueBuilder.durable(POKEMON_EVOLVED_QUEUE).build();
    }

    @Bean
    public Queue battleFinishedQueue() {
        return QueueBuilder.durable(BATTLE_FINISHED_QUEUE).build();
    }

    @Bean
    public Binding pokemonCapturedBinding() {
        return BindingBuilder.bind(pokemonCapturedQueue())
                .to(pokemonExchange())
                .with(POKEMON_CAPTURED_KEY);
    }

    @Bean
    public Binding pokemonEvolvedBinding() {
        return BindingBuilder.bind(pokemonEvolvedQueue())
                .to(pokemonExchange())
                .with(POKEMON_EVOLVED_KEY);
    }

    @Bean
    public Binding battleFinishedBinding() {
        return BindingBuilder.bind(battleFinishedQueue())
                .to(pokemonExchange())
                .with(BATTLE_FINISHED_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}