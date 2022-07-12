package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AIProductionIncreaseEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_NEXT_TURN;

@Service
public class AIProductionIncreaseExecutor implements EventExecutor<AIProductionIncreaseEvent>{

    @Override
    public EventExecutionResult execute(List<AIProductionIncreaseEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var playersProductionAI = context.player().getProductionAI();
        if (!playersProductionAI.isActivated()) {
            return new EventExecutionResult(context, new ArrayList<>());
        }
        var currentProduction = playersProductionAI.getCurrentProduction();
        var toIncreaseBy = context.gameBalance().upgrades().production().getBalanceConfig().getProductionAiIncreasePerTurn();
        playersProductionAI.setCurrentProduction(currentProduction + toIncreaseBy);
        
        var nextEvent = new AIProductionIncreaseEvent(END_OF_NEXT_TURN);
        context.game().getEventQueue().add(nextEvent);

        return new EventExecutionResult(context, new ArrayList<>());
    }
}
