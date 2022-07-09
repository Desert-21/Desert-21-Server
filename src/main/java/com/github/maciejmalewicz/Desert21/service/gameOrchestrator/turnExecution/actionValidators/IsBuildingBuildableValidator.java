package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsBuildingBuildableValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IsBuildingBuildableValidator implements ActionValidator<IsBuildingBuildableValidatable> {

    @Override
    public boolean validate(List<IsBuildingBuildableValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(IsBuildingBuildableValidatable validatable) {
        var buildingType = validatable.buildingType();
        return buildingType == BuildingType.METAL_FACTORY
                || buildingType == BuildingType.BUILDING_MATERIALS_FACTORY
                || buildingType == BuildingType.ELECTRICITY_FACTORY
                || buildingType == BuildingType.TOWER;
    }
}
