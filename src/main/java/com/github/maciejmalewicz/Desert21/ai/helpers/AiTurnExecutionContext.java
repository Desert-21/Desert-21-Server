package com.github.maciejmalewicz.Desert21.ai.helpers;

import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;

public record AiTurnExecutionContext(GameEnhancementWrapper game, Player player, GameBalanceDto gameBalance) {
}
