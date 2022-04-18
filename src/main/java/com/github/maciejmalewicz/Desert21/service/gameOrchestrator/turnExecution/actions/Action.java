package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ActionValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;

import java.util.ArrayList;
import java.util.List;

public interface Action {
    default List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        return new ArrayList<>();
    }

    default List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        return new ArrayList<>();
    }
}
