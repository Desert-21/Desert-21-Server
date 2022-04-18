package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingUpgradeExecutor implements EventExecutor<BuildingUpgradeEvent>{

    @Override
    public TurnExecutionContext execute(List<BuildingUpgradeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        events.stream()
                .map(BuildingUpgradeEvent::getLocation)
                .forEach(location -> {
                    try {
                        var building = BoardUtils.fieldAtLocation(context.game().getFields(), location).getBuilding();
                        var nextLevel = building.getLevel() + 1;
                        building.setLevel(nextLevel);
                    } catch (NotAcceptableException e) {
                        //ignore - validated before
                    }
                });
        return context;
    }
}
