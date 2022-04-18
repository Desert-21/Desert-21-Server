package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.BuildingUpgradeExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.EventExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.PaymentExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GameEventsExecutionService {

    private List<EventExecutor<?>> executors;

    public GameEventsExecutionService(
           PaymentExecutor paymentExecutor,
           BuildingUpgradeExecutor buildingUpgradeExecutor
    ) {
        executors = List.of(paymentExecutor, buildingUpgradeExecutor);
    }

    public TurnExecutionContext executeEvents(Collection<Action> actions, TurnExecutionContext context) throws NotAcceptableException {
        //gather all events
        var events = new ArrayList<GameEvent>();
        for (Action action: actions) {
            var eventsFromAction = action.getEventExecutables(context);
            events.addAll(eventsFromAction);
        }
        var eventsFromGameQueue = context.game().getEventQueue();
        events.addAll(eventsFromGameQueue);

        //tick the clock
        events.forEach(GameEvent::nextTurn);

        //separate events to be executed now from ones in the future
        var currentTurnEvents = events.stream()
                .filter(GameEvent::shouldTriggerNow)
                .toList();
        var otherTurnEvents = events.stream()
                .filter(e -> !e.shouldTriggerNow())
                .toList();

        //execute current events
        for (EventExecutor<?> executor: executors) {
            context = executeSingleExecutor(executor, currentTurnEvents, context);
        }

        //save future ones
        context.game().setEventQueue(otherTurnEvents);
        return context;
    }

    private <T extends GameEvent> TurnExecutionContext executeSingleExecutor(
            EventExecutor<T> executor,
            List<GameEvent> events,
            TurnExecutionContext context
    ) throws NotAcceptableException {
        var selectedEvents = events.stream()
                .filter(v -> v.getClass().equals(executor.getExecutableClass()))
                .map(executor.getExecutableClass()::cast)
                .toList();
        return executor.execute(selectedEvents, context);
    }
}
