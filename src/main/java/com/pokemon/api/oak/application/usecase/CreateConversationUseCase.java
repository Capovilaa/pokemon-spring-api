package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.infrastructure.ai.ConversationStore;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateConversationUseCase extends BaseUseCase<Void, String> {

    private final ConversationStore conversationStore;

    @Override
    public String execute(Void input, ExecutionContext context) {
        String conversationId = UUID.randomUUID().toString();
        conversationStore.createConversation(conversationId);

        log.info("New conversation created: {} for trainer '{}'",
                conversationId,
                context.user() != null ? context.user().username() : "anonymous");

        return conversationId;
    }
}