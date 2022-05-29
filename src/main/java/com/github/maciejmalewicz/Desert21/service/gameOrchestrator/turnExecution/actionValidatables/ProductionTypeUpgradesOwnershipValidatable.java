package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.TrainingMode;

public record ProductionTypeUpgradesOwnershipValidatable(TrainingMode trainingMode) implements ActionValidatable {
}
