package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import com.github.maciejmalewicz.Desert21.models.balance.lab.CombatBranchBalanceConfig;
import lombok.Getter;

import java.util.List;

@Getter
public class CombatBranchConfig implements LabBranchConfig {

    private final CombatBranchBalanceConfig combatBranchConfig;

    public CombatBranchConfig(CombatBranchBalanceConfig combatBranchConfig) {
        this.combatBranchConfig = combatBranchConfig;
    }

    @Override
    public LabUpgrade getBaseUpgrade() {
        return LabUpgrade.REUSABLE_PARTS;
    }

    @Override
    public List<LabUpgrade> getFirstTierUpgrades() {
        return List.of(
                LabUpgrade.MEDIUM_PRODUCTION,
                LabUpgrade.IMPROVED_DROIDS,
                LabUpgrade.IMPROVED_TANKS
        );
    }

    @Override
    public List<LabUpgrade> getSecondTierUpgrades() {
        return List.of(
                LabUpgrade.MASS_PRODUCTION,
                LabUpgrade.IMPROVED_CANNONS
        );
    }

    @Override
    public LabUpgrade getSuperUpgrade() {
        return LabUpgrade.ADVANCED_TACTICS;
    }
}
