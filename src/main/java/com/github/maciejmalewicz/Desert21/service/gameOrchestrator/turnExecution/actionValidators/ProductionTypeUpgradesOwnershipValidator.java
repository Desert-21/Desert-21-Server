package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ProductionTypeUpgradesOwnershipValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.TrainingMode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionTypeUpgradesOwnershipValidator implements ActionValidator<ProductionTypeUpgradesOwnershipValidatable> {

    @Override
    public boolean validate(List<ProductionTypeUpgradesOwnershipValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(v -> validateSingle(v, context));
    }

    private boolean validateSingle(ProductionTypeUpgradesOwnershipValidatable validatable, TurnExecutionContext context) {
        var trainingMode = validatable.trainingMode();
        if (trainingMode.equals(TrainingMode.SMALL_PRODUCTION)) {
            return true;
        }

        var player = context.player();
        if (trainingMode.equals(TrainingMode.MEDIUM_PRODUCTION)) {
            return player.ownsUpgrade(LabUpgrade.MEDIUM_PRODUCTION);
        }
        if (trainingMode.equals(TrainingMode.MASS_PRODUCTION)) {
            return player.ownsUpgrade(LabUpgrade.MASS_PRODUCTION);
        }
        return false;
    }
}
