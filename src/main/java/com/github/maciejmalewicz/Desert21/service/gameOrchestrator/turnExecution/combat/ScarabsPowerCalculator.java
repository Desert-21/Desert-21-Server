package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import org.springframework.stereotype.Service;

@Service
public class ScarabsPowerCalculator {

    public int calculateScarabsPower(int numberOfScarabs, TurnExecutionContext context) {
        var singleScarabPower = context.gameBalance().combat().scarabs().getPower();
        var scarabsPower = numberOfScarabs * singleScarabPower;
        var attackerOwnsScarabScanners = context.player().ownsUpgrade(LabUpgrade.SCARAB_SCANNERS);

        if (!attackerOwnsScarabScanners) {
            return scarabsPower;
        }

        var scarabScannersBonus = context.gameBalance().upgrades().control().getControlBranchConfig().getScarabScannersPowerDecreaseRatio();
        var scarabsPowerRatio = 1 - scarabScannersBonus;
        return (int) Math.round(scarabsPower * scarabsPowerRatio);
    }
}
