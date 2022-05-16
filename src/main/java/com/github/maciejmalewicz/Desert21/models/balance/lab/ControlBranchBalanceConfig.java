package com.github.maciejmalewicz.Desert21.models.balance.lab;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ControlBranchBalanceConfig {
    private double scarabScannersPowerDecreaseRatio;

    private int goldDiggersProductionPerFieldBonus;

    private int towerCreatorMaxTowersBuilt;
}
