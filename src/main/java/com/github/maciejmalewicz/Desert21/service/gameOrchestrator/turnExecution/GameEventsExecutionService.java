package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
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
           BuildingUpgradeExecutor buildingUpgradeExecutor,
           ResourcesProductionExecutor resourcesProductionExecutor,
           ArmyTrainingExecutor armyTrainingExecutor,
           ArmyLeavingExecutor armyLeavingExecutor,
           ArmyEnteringExecutor armyEnteringExecutor,
           AttackingExecutor attackingExecutor,
           LabUpgradeExecutor labUpgradeExecutor,
           RocketStrikeExecutor rocketStrikeExecutor,
           BuildBuildingExecutor buildBuildingExecutor,
           BombardingExecutor bombardingExecutor
    ) {
        executors = List.of(
                paymentExecutor,
                buildingUpgradeExecutor,
                resourcesProductionExecutor,
                armyTrainingExecutor,
                armyLeavingExecutor,
                armyEnteringExecutor,
                attackingExecutor,
                labUpgradeExecutor,
                rocketStrikeExecutor,
                buildBuildingExecutor,
                bombardingExecutor
        );
    }

    public EventExecutionResult executeEvents(Collection<Action> actions, TurnExecutionContext context) throws NotAcceptableException {
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

        var eventResults = new ArrayList<EventResult>();
        //execute current events
        for (EventExecutor<?> executor: executors) {
            var executionResultPair = executeSingleExecutor(executor, currentTurnEvents, context);
            context = executionResultPair.context();
            eventResults.addAll(executionResultPair.results());
        }

        //save future ones
        context.game().setEventQueue(otherTurnEvents);
        return new EventExecutionResult(context, eventResults);
    }

    private <T extends GameEvent> EventExecutionResult executeSingleExecutor(
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
