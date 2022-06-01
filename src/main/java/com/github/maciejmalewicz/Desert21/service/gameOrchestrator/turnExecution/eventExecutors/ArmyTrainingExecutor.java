package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.ArmyTrainingEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArmyTrainingExecutor implements EventExecutor<ArmyTrainingEvent> {

    @Override
    public EventExecutionResult execute(List<ArmyTrainingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var eventResults = new ArrayList<EventResult>();

        for (ArmyTrainingEvent event: events) {
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), event.getLocation());
            var currentArmy = field.getArmy();
            var upgradedArmy = extendArmyWithNewTrainedUnits(currentArmy, event);
            field.setArmy(upgradedArmy);

            var eventResult = new ArmyTrainingEventResult(
                    event.getLocation(),
                    event.getUnitType(),
                    event.getAmount()
            );
            eventResults.add(eventResult);
        }
        return new EventExecutionResult(context, eventResults);
    }

    private Army extendArmyWithNewTrainedUnits(Army currentArmy, ArmyTrainingEvent event) {
        return switch (event.getUnitType()) {
            case DROID -> new Army(
                    currentArmy.getDroids() + event.getAmount(),
                    currentArmy.getTanks(),
                    currentArmy.getCannons()
            );
            case TANK -> new Army(
                    currentArmy.getDroids(),
                    currentArmy.getTanks() + event.getAmount(),
                    currentArmy.getCannons()
            );
            case CANNON -> new Army(
                    currentArmy.getDroids(),
                    currentArmy.getTanks(),
                    currentArmy.getCannons() + event.getAmount()
            );
        };
    }
}
