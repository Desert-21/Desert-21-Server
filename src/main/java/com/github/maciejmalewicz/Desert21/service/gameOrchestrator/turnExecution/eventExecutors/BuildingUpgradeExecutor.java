package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BuildingUpgradeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BuildingUpgradeExecutor implements EventExecutor<BuildingUpgradeEvent>{

    @Override
    public EventExecutionResult execute(List<BuildingUpgradeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var results = events.stream()
                .map(BuildingUpgradeEvent::getLocation)
                .map(location -> {
                    try {
                        var building = BoardUtils.fieldAtLocation(context.game().getFields(), location).getBuilding();
                        var currentLevel = building.getLevel();
                        var nextLevel = currentLevel + 1;
                        building.setLevel(nextLevel);
                        return new BuildingUpgradeEventResult(currentLevel, nextLevel, location);
                    } catch (NotAcceptableException e) {
                        //ignore - validated before
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(EventResult.class::cast)
                .toList();
        return new EventExecutionResult(context, results);
    }
}
