package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.application.OakPersonality;
import com.pokemon.api.oak.infrastructure.ai.ConversationStore;
import com.pokemon.api.oak.infrastructure.ai.tools.GetBattleHistoryTool;
import com.pokemon.api.oak.infrastructure.ai.tools.GetRankingTool;
import com.pokemon.api.oak.infrastructure.ai.tools.GetTrainerPokedexTool;
import com.pokemon.api.oak.infrastructure.ai.tools.GetTrainerPokemonsTool;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AskOakUseCase extends BaseUseCase<AskOakUseCase.Input, OakResponse> {

    private final ChatModel chatModel;
    private final ConversationStore conversationStore;
    private final GetTrainerPokemonsTool getTrainerPokemonsTool;
    private final GetBattleHistoryTool getBattleHistoryTool;
    private final GetTrainerPokedexTool getTrainerPokedexTool;
    private final GetRankingTool getRankingTool;

    public AskOakUseCase(
            @Qualifier("anthropicChatModel") ChatModel chatModel,
            ConversationStore conversationStore,
            GetTrainerPokemonsTool getTrainerPokemonsTool,
            GetBattleHistoryTool getBattleHistoryTool,
            GetTrainerPokedexTool getTrainerPokedexTool,
            GetRankingTool getRankingTool) {
        this.chatModel = chatModel;
        this.conversationStore = conversationStore;
        this.getTrainerPokemonsTool = getTrainerPokemonsTool;
        this.getBattleHistoryTool = getBattleHistoryTool;
        this.getTrainerPokedexTool = getTrainerPokedexTool;
        this.getRankingTool = getRankingTool;
    }

    public record Input(String question, String conversationId) {
    }

    @Override
    public OakResponse execute(Input input, ExecutionContext context) {
        log.info("Professor Oak received question from '{}' [conversationId={}]: {}",
                context.user() != null ? context.user().username() : "anonymous",
                input.conversationId() != null ? input.conversationId() : "none",
                input.question());

        if (input.conversationId() != null && !conversationStore.exists(input.conversationId())) {
            throw new NotFoundException("Conversation", input.conversationId());
        }

        String systemPromptWithContext = OakPersonality.SYSTEM_PROMPT + """
                
                Current trainer context:
                - Trainer ID (keycloakId): %s
                - Trainer username: %s
                
                When the trainer asks about their own data (their Pokémon, battles, Pokédex),
                always use the available tools with their keycloakId to fetch real data before answering.
                """.formatted(
                context.user() != null ? context.user().id() : "unknown",
                context.user() != null ? context.user().username() : "anonymous"
        );

        try {
            var options = ToolCallingChatOptions.builder()
                    .toolCallbacks(
                            FunctionToolCallback.builder("getTrainerPokemons", getTrainerPokemonsTool)
                                    .description("Get all Pokémon owned by the current trainer, including their stats, types and level")
                                    .inputType(GetTrainerPokemonsTool.Request.class)
                                    .build(),
                            FunctionToolCallback.builder("getBattleHistory", getBattleHistoryTool)
                                    .description("Get the battle history of the current trainer, showing wins, losses, Pokémon used and opponent details")
                                    .inputType(GetBattleHistoryTool.Request.class)
                                    .build(),
                            FunctionToolCallback.builder("getTrainerPokedex", getTrainerPokedexTool)
                                    .description("Get the Pokédex of the current trainer, showing all species they have already encountered and caught")
                                    .inputType(GetTrainerPokedexTool.Request.class)
                                    .build(),
                            FunctionToolCallback.builder("getRanking", getRankingTool)
                                    .description("Get the global trainer ranking ordered by wins, showing username, wins, losses and win rate")
                                    .inputType(GetRankingTool.Request.class)
                                    .build()
                    )
                    .build();

            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemPromptWithContext));

            if (input.conversationId() != null) {
                messages.addAll(conversationStore.getHistory(input.conversationId()));
            }

            messages.add(new UserMessage(input.question()));

            var prompt = new Prompt(messages, options);
            var response = chatModel.call(prompt);
            var metadata = response.getMetadata();
            String answer = response.getResult().getOutput().getText();

            if (input.conversationId() != null) {
                conversationStore.addUserMessage(input.conversationId(), input.question());
                conversationStore.addAssistantMessage(input.conversationId(), answer);
            }

            return OakResponse.builder()
                    .answer(answer)
                    .model(metadata.getModel())
                    .inputTokens(metadata.getUsage() != null
                            ? metadata.getUsage().getPromptTokens() : 0)
                    .outputTokens(metadata.getUsage() != null
                            ? metadata.getUsage().getCompletionTokens() : 0)
                    .build();

        } catch (Exception e) {
            log.error("Erro ao chamar Oak. Tipo: {}. Mensagem: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
}