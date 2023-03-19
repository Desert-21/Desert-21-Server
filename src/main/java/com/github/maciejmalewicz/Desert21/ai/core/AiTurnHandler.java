package com.github.maciejmalewicz.Desert21.ai.core;

import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AiTurnHandler {

    private final AiPlayerConfig aiPlayerConfig;
    private final GameBalanceService gameBalanceService;
    private final AiTurnExecutor aiTurnExecutor;

    public AiTurnHandler(AiPlayerConfig aiPlayerConfig, GameBalanceService gameBalanceService, @Lazy AiTurnExecutor aiTurnExecutor) {
        this.aiPlayerConfig = aiPlayerConfig;
        this.gameBalanceService = gameBalanceService;
        this.aiTurnExecutor = aiTurnExecutor;
    }

    public void handleTurn(Game game) {
        var player = game.getPlayers().stream()
                .filter(p -> aiPlayerConfig.getId().equals(p.getId()))
                .findFirst()
                .orElseThrow();
        aiTurnExecutor.executeTurn(game, player, gameBalanceService.getGameBalance());
    }
}
