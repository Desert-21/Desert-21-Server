package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import org.springframework.stereotype.Service;

@Service
public class BombardingAttackersPowerCalculator {

    public int calculateAttackersPower(int cannons, TurnExecutionContext context) {
        var balance = context.gameBalance();
        var singleCannonPower = balance.combat().cannons().getPower();
        var rawCannonsPower = cannons * singleCannonPower;
        var bombardingPowerFraction = balance.upgrades().combat().getBalanceConfig()
                .getImprovedCannonsBombardingPowerFraction();
        return (int) Math.round(rawCannonsPower * bombardingPowerFraction);
    }
}
