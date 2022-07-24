package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.models.Location;

public record HasSufficientLabForBuildingUpgradeValidatable(Location location) implements ActionValidatable {
}
