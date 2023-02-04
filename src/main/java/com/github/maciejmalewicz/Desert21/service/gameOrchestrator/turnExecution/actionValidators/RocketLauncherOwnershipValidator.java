package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.RocketLauncherOwnershipValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.RocketCostCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RocketLauncherOwnershipValidator implements ActionValidator<RocketLauncherOwnershipValidatable> {

    @Override
    public boolean validate(List<RocketLauncherOwnershipValidatable> validatables, TurnExecutionContext context) {
        var optionalValidatable = validatables.stream().findFirst();
        if (optionalValidatable.isEmpty()) {
            return true;
        }
        var fields = optionalValidatable.get().ownedFields();
        return fields.stream()
                .anyMatch(RocketCostCalculator::isRocketLauncher);
    }
}
