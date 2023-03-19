package com.github.maciejmalewicz.Desert21.config;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "general.ai-player")
@Data
@Component
public class AiPlayerConfig {

    private String id;
    private String name;

    public boolean isAiGame(Game game) {
       return game.getPlayers().stream()
               .anyMatch(p -> id.equals(p.getId()));
    }

    public boolean isAiTurn(Game game) {
        return id.equals(game.getStateManager().getCurrentPlayerId());
    }
}
