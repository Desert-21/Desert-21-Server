package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import org.springframework.stereotype.Service;

@Service
public class AttackersArmyAfterAttackCalculator {

    public FightingArmy calculateAttackersArmyAfter(FightingArmy attackersBefore, boolean attackerHasWon, double destructionRatio, Player attacker, GameBalanceDto gameBalance) {
        if (!attackerHasWon) {
            return new FightingArmy(0, 0, 0, 0);
        }
        double reusablePartsDamageRatio = 1 - gameBalance.upgrades().combat().getBalanceConfig().getReusablePartsUnitsFractionSaved();
        double actualDestructionRatio = attacker.ownsUpgrade(LabUpgrade.REUSABLE_PARTS)
                ? destructionRatio * reusablePartsDamageRatio
                : destructionRatio;
        double remainingUnitsRatio = 1 - actualDestructionRatio;

        var droids = (int) Math.floor(attackersBefore.droids() * remainingUnitsRatio);
        var tanks = (int) Math.floor(attackersBefore.tanks() * remainingUnitsRatio);
        var cannons = (int) Math.floor(attackersBefore.cannons() * remainingUnitsRatio);
        return new FightingArmy(droids, tanks, cannons, 0);
    }
}
