package com.pokemon.api.oak.infrastructure.ai;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationStore {

    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();

    public void createConversation(String conversationId) {
        conversations.put(conversationId, new ArrayList<>());
    }

    public boolean exists(String conversationId) {
        return conversations.containsKey(conversationId);
    }

    public List<Message> getHistory(String conversationId) {
        return conversations.getOrDefault(conversationId, List.of());
    }

    public void addUserMessage(String conversationId, String content) {
        conversations.computeIfAbsent(conversationId, k -> new ArrayList<>())
                .add(new UserMessage(content));
    }

    public void addAssistantMessage(String conversationId, String content) {
        conversations.computeIfAbsent(conversationId, k -> new ArrayList<>())
                .add(new AssistantMessage(content));
    }

    public void deleteConversation(String conversationId) {
        conversations.remove(conversationId);
    }
}