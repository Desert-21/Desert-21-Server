package com.github.maciejmalewicz.Desert21.ai.actionsPickers;

import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;

import java.util.List;

public interface ActionsPicker {
    List<Action> getActions(AiTurnExecutionContext turnExecutionContext);
}
