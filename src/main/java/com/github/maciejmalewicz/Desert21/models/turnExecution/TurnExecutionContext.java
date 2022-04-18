package com.github.maciejmalewicz.Desert21.models.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;

public record TurnExecutionContext(GameBalanceDto gameBalance, Game game, Player player) {
}
