package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.battle.domain.entity.Battle;
import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.oak.application.OakPersonality;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AnalyzeBattleUseCase extends BaseUseCase<Long, OakResponse> {

    private final ChatModel chatModel;
    private final BattleRepository battleRepository;

    public AnalyzeBattleUseCase(
            @Qualifier("anthropicChatModel") ChatModel chatModel,
            BattleRepository battleRepository) {
        this.chatModel = chatModel;
        this.battleRepository = battleRepository;
    }

    @Override
    public OakResponse execute(Long battleId, ExecutionContext context) {
        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new NotFoundException("Battle", battleId));

        String battleSummary = buildBattleSummary(battle);
        log.info("Professor Oak analyzing battle {}", battleId);

        var prompt = new Prompt(List.of(
                new SystemMessage(OakPersonality.SYSTEM_PROMPT),
                new UserMessage(battleSummary)
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

    private String buildBattleSummary(Battle battle) {
        var sb = new StringBuilder();
        sb.append("Please analyze this Pokémon battle as Professor Oak:\n\n");
        sb.append("Battle #").append(battle.getId()).append("\n");
        sb.append("Attacker: ").append(battle.getAttackerTrainer().getUsername())
                .append(" with ").append(battle.getAttackerPokemon().getName())
                .append(" (Level ").append(battle.getAttackerPokemon().getLevel()).append(")\n");
        sb.append("Defender: ").append(battle.getDefenderTrainer().getUsername())
                .append(" with ").append(battle.getDefenderPokemon().getName())
                .append(" (Level ").append(battle.getDefenderPokemon().getLevel()).append(")\n");
        sb.append("Winner: ").append(battle.getWinnerTrainer().getUsername())
                .append(" with ").append(battle.getWinnerPokemon().getName()).append("\n");
        sb.append("Total turns: ").append(battle.getTotalTurns()).append("\n\n");

        sb.append("Turn by turn:\n");
        battle.getTurns().forEach(turn -> {
            sb.append("Turn ").append(turn.getTurnNumber()).append(": ")
                    .append(turn.getAttackerPokemon().getName())
                    .append(" dealt ").append(turn.getDamage()).append(" damage. ")
                    .append(turn.getDefenderPokemon().getName())
                    .append(" has ").append(turn.getDefenderHpLeft()).append(" HP left.\n");
        });

        sb.append("\nPlease give a brief analysis: who had the advantage, was it close, and what could the loser have done differently?");
        return sb.toString();
    }
}