package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.Building;

public record BuildingUpgradableValidatable(Building building) implements ActionValidatable {
}
