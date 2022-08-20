package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BuildingBuiltEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BuildingUpgradeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.fieldAtLocation;

@Service
public class BuildBuildingExecutor implements EventExecutor<BuildBuildingEvent> {

    @Override
    public EventExecutionResult execute(List<BuildBuildingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var results = new ArrayList<EventResult>();
        for (BuildBuildingEvent event: events) {
            var location = event.getLocation();

            var field = fieldAtLocation(context.game().getFields(), location);
            var building = new Building(event.getBuildingType(), 1);
            field.setBuilding(building);
            updatePlayerCaps(context.player(), building);

            results.add(new BuildingBuiltEventResult(event.getLocation(), event.getBuildingType()));
        }
        return new EventExecutionResult(context, results);
    }

    private void updatePlayerCaps(Player player, Building building) {
        if (building.isDefensive()) {
            player.setBuiltTowers(player.getBuiltTowers() + 1);
        } else {
            player.setBuiltFactories(player.getBuiltFactories() + 1);
        }
    }

}
