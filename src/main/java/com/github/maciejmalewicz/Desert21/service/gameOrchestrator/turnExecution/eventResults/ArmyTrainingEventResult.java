package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.UnitType;

public record ArmyTrainingEventResult(Location location, UnitType unitType, int amount) implements EventResult {
    @Override
    public long millisecondsToView() {
        return 2000;
    }
}
