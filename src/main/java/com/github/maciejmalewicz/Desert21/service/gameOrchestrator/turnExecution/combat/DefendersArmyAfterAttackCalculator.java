package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import org.springframework.stereotype.Service;

@Service
public class DefendersArmyAfterAttackCalculator {

    public FightingArmy calculateDefendersArmyAfter(FightingArmy defendersBefore, boolean attackerHasWon, double destructionRatio) {
        if (attackerHasWon) {
            return new FightingArmy(0, 0, 0, 0);
        }
        double remainingUnitsRatio = 1 - destructionRatio;

        var droids = (int) Math.floor(defendersBefore.droids() * remainingUnitsRatio);
        var tanks = (int) Math.floor(defendersBefore.tanks() * remainingUnitsRatio);
        var cannons = (int) Math.floor(defendersBefore.cannons() * remainingUnitsRatio);
        var scarabs = (int) Math.floor(defendersBefore.scarabs() * remainingUnitsRatio);
        return new FightingArmy(droids, tanks, cannons, scarabs);
    }
}
