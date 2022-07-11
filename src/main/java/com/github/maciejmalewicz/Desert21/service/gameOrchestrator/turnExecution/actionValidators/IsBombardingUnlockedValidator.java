package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsBombardingUnlockedValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IsBombardingUnlockedValidator implements ActionValidator<IsBombardingUnlockedValidatable> {

    @Override
    public boolean validate(List<IsBombardingUnlockedValidatable> validatables, TurnExecutionContext context) {
        if (validatables.size() < 1) {
            return true;
        }
        return context.player().ownsUpgrade(LabUpgrade.IMPROVED_CANNONS);
    }
}
