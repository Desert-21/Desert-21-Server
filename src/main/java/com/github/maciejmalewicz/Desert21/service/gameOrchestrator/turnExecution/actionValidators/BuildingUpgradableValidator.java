package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.BuildingUpgradableValidatable;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingUpgradableValidator implements ActionValidator<BuildingUpgradableValidatable> {

    @Override
    public boolean validate(List<BuildingUpgradableValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream()
                .allMatch(validatable -> validateSingle(validatable, context));
    }

    private boolean validateSingle(BuildingUpgradableValidatable validatable, TurnExecutionContext context) {
        var building = validatable.building();
        var level = building.getLevel();
        var nextLevel = level + 1;
        try {
            var config = BuildingUtils.buildingTypeToConfig(
                    building.getType(),
                    context.gameBalance()
            );
            var cost = config.costAtLevel(nextLevel);
            return cost != -1;
        } catch (NotAcceptableException e) {
            return false;
        }
    }
}
