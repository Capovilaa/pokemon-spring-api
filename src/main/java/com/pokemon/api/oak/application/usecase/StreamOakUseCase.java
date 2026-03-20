package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.application.OakPersonality;
import com.pokemon.api.oak.infrastructure.ai.tools.GetBattleHistoryTool;
import com.pokemon.api.oak.infrastructure.ai.tools.GetRankingTool;
import com.pokemon.api.oak.infrastructure.ai.tools.GetTrainerPokedexTool;
import com.pokemon.api.oak.infrastructure.ai.tools.GetTrainerPokemonsTool;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StreamOakUseCase extends BaseUseCase<AskOakUseCase.Input, Flux<String>> {

    private final ChatModel chatModel;
    private final GetTrainerPokemonsTool getTrainerPokemonsTool;
    private final GetBattleHistoryTool getBattleHistoryTool;
    private final GetTrainerPokedexTool getTrainerPokedexTool;
    private final GetRankingTool getRankingTool;

    public StreamOakUseCase(
            @Qualifier("anthropicChatModel") ChatModel chatModel,
            GetTrainerPokemonsTool getTrainerPokemonsTool,
            GetBattleHistoryTool getBattleHistoryTool,
            GetTrainerPokedexTool getTrainerPokedexTool,
            GetRankingTool getRankingTool) {
        this.chatModel = chatModel;
        this.getTrainerPokemonsTool = getTrainerPokemonsTool;
        this.getBattleHistoryTool = getBattleHistoryTool;
        this.getTrainerPokedexTool = getTrainerPokedexTool;
        this.getRankingTool = getRankingTool;
    }

    @Override
    public Flux<String> execute(AskOakUseCase.Input input, ExecutionContext context) {
        log.info("Professor Oak streaming answer to '{}': {}", context.user() != null ? context.user().username() : "anonymous", input.question());

        String systemPromptWithContext = OakPersonality.SYSTEM_PROMPT + """
                
                Current trainer context:
                - Trainer ID (keycloakId): %s
                - Trainer username: %s
                
                When the trainer asks about their own data (their Pokémon, battles, Pokédex),
                always use the available tools with their keycloakId to fetch real data before answering.
                """.formatted(context.user() != null ? context.user().id() : "unknown", context.user() != null ? context.user().username() : "anonymous");

        var options = ToolCallingChatOptions.builder().toolCallbacks(FunctionToolCallback.builder("getTrainerPokemons", getTrainerPokemonsTool).description("Get all Pokémon owned by the current trainer, including their stats, types and level").inputType(GetTrainerPokemonsTool.Request.class).build(), FunctionToolCallback.builder("getBattleHistory", getBattleHistoryTool).description("Get the battle history of the current trainer, showing wins, losses, Pokémon used and opponent details").inputType(GetBattleHistoryTool.Request.class).build(), FunctionToolCallback.builder("getTrainerPokedex", getTrainerPokedexTool).description("Get the Pokédex of the current trainer, showing all species they have already encountered and caught").inputType(GetTrainerPokedexTool.Request.class).build(), FunctionToolCallback.builder("getRanking", getRankingTool).description("Get the global trainer ranking ordered by wins, showing username, wins, losses and win rate").inputType(GetRankingTool.Request.class).build()).build();

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPromptWithContext));
        messages.add(new UserMessage(input.question()));

        var prompt = new Prompt(messages, options);

        return chatModel.stream(prompt)
                .filter(response -> response != null
                        && response.getResult() != null
                        && response.getResult().getOutput() != null
                        && response.getResult().getOutput().getText() != null
                        && !response.getResult().getOutput().getText().isEmpty())
                .map(response -> response.getResult().getOutput().getText())
                .onErrorResume(e -> {
                    log.error("Erro no streaming: {}", e.getMessage(), e);
                    return Flux.just("[erro no streaming]");
                });
    }
}