package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LabUpgradeNotRepeatedValidatable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class LabUpgradeNotRepeatedValidator implements ActionValidator<LabUpgradeNotRepeatedValidatable> {

    @Override
    public boolean validate(List<LabUpgradeNotRepeatedValidatable> validatables, TurnExecutionContext context) {
        var upgrades = context.player().getOwnedUpgrades();

        var isRepeated = validatables.size() != validatables.stream()
                .map(LabUpgradeNotRepeatedValidatable::labUpgrade)
                .distinct()
                .count();
        if (isRepeated) {
            return false;
        }

        var upgradesSet = new HashSet<>(upgrades);
        return validatables.stream()
                .map(LabUpgradeNotRepeatedValidatable::labUpgrade)
                .noneMatch(upgradesSet::contains);
    }
}
