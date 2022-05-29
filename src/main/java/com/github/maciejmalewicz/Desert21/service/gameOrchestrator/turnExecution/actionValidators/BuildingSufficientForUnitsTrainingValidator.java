package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.BuildingSufficientForUnitsTrainingValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingSufficientForUnitsTrainingValidator implements ActionValidator<BuildingSufficientForUnitsTrainingValidatable> {

    @Override
    public boolean validate(List<BuildingSufficientForUnitsTrainingValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(BuildingSufficientForUnitsTrainingValidatable validatable) {
        var building = validatable.building();
        var type = building.getType();
        if (!type.equals(BuildingType.TOWER) && !type.equals(BuildingType.HOME_BASE)) {
            return false;
        }
        var unitType = validatable.unitType();
        var expectedLevel = switch (unitType) {
            case DROID -> 1;
            case TANK -> 2;
            case CANNON -> 3;
        };
        var buildingLevel = building.getLevel();
        return buildingLevel >= expectedLevel;
    }
}
