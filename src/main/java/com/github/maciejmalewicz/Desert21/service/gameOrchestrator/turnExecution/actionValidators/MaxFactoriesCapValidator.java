package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.MaxFactoriesCapValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaxFactoriesCapValidator implements ActionValidator<MaxFactoriesCapValidatable> {
    @Override
    public boolean validate(List<MaxFactoriesCapValidatable> validatables, TurnExecutionContext context) {
        var newFactoriesBuilt = validatables.size();
        if (newFactoriesBuilt < 1) {
            return true;
        }
        var builtFactories = context.player().getBuiltFactories();
        var factoryBuildingEvents = (int) context.game().getEventQueue().stream()
                .filter(e -> e.getClass() == BuildBuildingEvent.class)
                .map(BuildBuildingEvent.class::cast)
                .filter(e -> isFactoryType(e.getBuildingType()))
                .count();
        var totalSlotsTaken = newFactoriesBuilt + builtFactories + factoryBuildingEvents;
        var factoryCap = context.gameBalance().upgrades().production().getBalanceConfig().getFactoryBuildingMaxFactoriesBuilt();
        return totalSlotsTaken <= factoryCap;
    }

    private boolean isFactoryType(BuildingType buildingType) {
        var list = List.of(
                BuildingType.METAL_FACTORY,
                BuildingType.BUILDING_MATERIALS_FACTORY,
                BuildingType.ELECTRICITY_FACTORY
        );
        return list.contains(buildingType);
    }
}
