package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyLeavingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArmyLeavingExecutor implements EventExecutor<ArmyLeavingEvent> {

    @Override
    public EventExecutionResult execute(List<ArmyLeavingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        for (ArmyLeavingEvent event: events) {
            var location = event.getLocation();
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), location);
            var currentArmy = field.getArmy();
            var toDecreaseBy = event.getArmy();
            var decreasedArmy = new Army(
                    currentArmy.getDroids() - toDecreaseBy.getDroids(),
                    currentArmy.getTanks() - toDecreaseBy.getTanks(),
                    currentArmy.getCannons() - toDecreaseBy.getCannons()
            );
            field.setArmy(decreasedArmy);
        }
        return new EventExecutionResult(context, new ArrayList<>());
    }
}
