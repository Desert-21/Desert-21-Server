package com.github.maciejmalewicz.Desert21.models.balance.lab;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CombatBranchCostConfig {

    private int reusableParts;

    private int mediumProduction;
    private int improvedDroids;
    private int improvedTanks;

    private int massProduction;
    private int improvedCannons;

    private int advancedTactics;
}
