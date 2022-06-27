package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;

public record LabUpgradeNotRepeatedValidatable(LabUpgrade labUpgrade) implements ActionValidatable {
}
