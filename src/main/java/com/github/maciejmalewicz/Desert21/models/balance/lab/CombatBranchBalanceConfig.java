package com.github.maciejmalewicz.Desert21.models.balance.lab;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CombatBranchBalanceConfig {
    private double reusablePartsUnitsFractionSaved;

    private double improvedDroidsBaseDefenceBonus;
    private double improvedDroidsBaseAtTowerDefenceBonus;
    private double improvedTanksPowerBonus;

    private double improvedCannonsBombardingPowerFraction;

    private double advancedTacticsTowerBonusesDecrease;
}
