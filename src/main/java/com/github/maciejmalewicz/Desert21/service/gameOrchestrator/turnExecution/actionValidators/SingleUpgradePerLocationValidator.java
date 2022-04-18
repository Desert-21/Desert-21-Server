package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleUpgradePerLocationValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SingleUpgradePerLocationValidator implements ActionValidator<SingleUpgradePerLocationValidatable> {

    @Override
    public boolean validate(List<SingleUpgradePerLocationValidatable> validatables, TurnExecutionContext context) {
        return validatables.size() == validatables.stream()
                .map(SingleUpgradePerLocationValidatable::location)
                .distinct()
                .count();
    }
}
