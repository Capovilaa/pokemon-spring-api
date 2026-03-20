package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.application.OakPersonality;
import com.pokemon.api.oak.infrastructure.ai.PokemonKnowledgeService;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AskOakWithRagUseCase extends BaseUseCase<String, OakResponse> {

    private final ChatModel chatModel;
    private final PokemonKnowledgeService knowledgeService;

    public AskOakWithRagUseCase(
            @Qualifier("anthropicChatModel") ChatModel chatModel,
            PokemonKnowledgeService knowledgeService) {
        this.chatModel = chatModel;
        this.knowledgeService = knowledgeService;
    }

    @Override
    public OakResponse execute(String question, ExecutionContext context) {
        log.info("Oak RAG answering: {}", question);

        List<Document> relevantDocs = knowledgeService.search(question);

        String context_str = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        String systemWithContext = OakPersonality.SYSTEM_PROMPT + """
                
                You have access to the following Pokédex knowledge to help answer questions.
                Use this information when relevant:
                
                %s
                """.formatted(context_str);

        var prompt = new Prompt(List.of(
                new SystemMessage(systemWithContext),
                new UserMessage(question)
        ));

        var response = chatModel.call(prompt);
        var metadata = response.getMetadata();

        return OakResponse.builder()
                .answer(response.getResult().getOutput().getText())
                .model(metadata.getModel())
                .inputTokens(metadata.getUsage() != null
                        ? metadata.getUsage().getPromptTokens() : 0)
                .outputTokens(metadata.getUsage() != null
                        ? metadata.getUsage().getCompletionTokens() : 0)
                .build();
    }
}