package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;

public interface ActionValidator {
    boolean validate(Action action, GameBalanceDto gameBalance, Game game);
}
