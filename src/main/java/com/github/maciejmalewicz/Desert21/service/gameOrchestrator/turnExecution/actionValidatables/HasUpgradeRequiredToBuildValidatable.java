package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.models.BuildingType;

public record HasUpgradeRequiredToBuildValidatable(BuildingType buildingType) implements ActionValidatable {
}
