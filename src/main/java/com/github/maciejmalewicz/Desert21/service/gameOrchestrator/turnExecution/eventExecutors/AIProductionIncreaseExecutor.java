package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AIProductionIncreaseEvent;

import java.util.List;

public class AIProductionIncreaseExecutor implements EventExecutor<AIProductionIncreaseEvent>{

    //todo: implement!
    @Override
    public EventExecutionResult execute(List<AIProductionIncreaseEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        return null;
    }
}
