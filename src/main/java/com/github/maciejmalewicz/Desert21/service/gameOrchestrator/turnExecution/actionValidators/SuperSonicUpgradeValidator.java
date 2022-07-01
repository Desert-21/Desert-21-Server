package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SuperSonicUpgradeValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperSonicUpgradeValidator implements ActionValidator<SuperSonicUpgradeValidatable> {

    @Override
    public boolean validate(List<SuperSonicUpgradeValidatable> validatables, TurnExecutionContext context) {
        var optionalValidatable = validatables.stream().findFirst();
        if (optionalValidatable.isEmpty()) {
            return true;
        }
        var isAttackingRocket = optionalValidatable.get().isTargetingRocket();
        if (!isAttackingRocket) {
            return true;
        }
        return context.player().ownsUpgrade(LabUpgrade.SUPER_SONIC_ROCKETS);
    }
}
