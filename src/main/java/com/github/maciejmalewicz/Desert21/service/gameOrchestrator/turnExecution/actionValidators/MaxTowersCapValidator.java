package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.MaxTowersCapValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaxTowersCapValidator implements ActionValidator<MaxTowersCapValidatable> {

    @Override
    public boolean validate(List<MaxTowersCapValidatable> validatables, TurnExecutionContext context) {
        var newTowersBuilt = validatables.size();
        if (newTowersBuilt < 1) {
            return true;
        }
        var builtTowers = context.player().getBuiltTowers();
        var towerBuildingEvents = (int) context.game().getEventQueue().stream()
                .filter(e -> e.getClass() == BuildBuildingEvent.class)
                .map(BuildBuildingEvent.class::cast)
                .filter(e -> e.getBuildingType() == BuildingType.TOWER)
                .count();
        var totalSlotsTaken = newTowersBuilt + builtTowers + towerBuildingEvents;
        var towerCap = context.gameBalance().upgrades().control().getBalanceConfig().getTowerCreatorMaxTowersBuilt();
        return totalSlotsTaken <= towerCap;
    }
}
