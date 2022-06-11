package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyEnteringEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArmyEnteringExecutor implements EventExecutor<ArmyEnteringEvent> {

    @Override
    public EventExecutionResult execute(List<ArmyEnteringEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        for (ArmyEnteringEvent event: events) {
            var location = event.getLocation();
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), location);
            var currentArmy = field.getArmy();
            var toIncreaseBy = event.getArmy();
            var increasedArmy = new Army(
                    currentArmy.getDroids() + toIncreaseBy.getDroids(),
                    currentArmy.getTanks() + toIncreaseBy.getTanks(),
                    currentArmy.getCannons() + toIncreaseBy.getCannons()
            );
            field.setArmy(increasedArmy);
        }
        return new EventExecutionResult(context, new ArrayList<>());
    }
}
