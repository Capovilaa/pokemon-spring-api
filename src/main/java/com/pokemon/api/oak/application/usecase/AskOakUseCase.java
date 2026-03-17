package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.application.OakPersonality;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AskOakUseCase extends BaseUseCase<String, OakResponse> {

    private final ChatModel chatModel;

    @Override
    public OakResponse execute(String question, ExecutionContext context) {
        log.info("Professor Oak received question from '{}': {}",
                context.user() != null ? context.user().username() : "anonymous",
                question);

        try {
            var prompt = new Prompt(List.of(
                    new SystemMessage(OakPersonality.SYSTEM_PROMPT),
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

        } catch (Exception e) {
            log.error("Erro ao chamar Gemini. Tipo: {}. Mensagem: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
}