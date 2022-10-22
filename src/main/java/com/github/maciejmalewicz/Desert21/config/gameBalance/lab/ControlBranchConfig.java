package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import com.github.maciejmalewicz.Desert21.models.balance.lab.ControlBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.ControlBranchCostConfig;
import lombok.Getter;

import java.util.List;

@Getter
public class ControlBranchConfig implements LabBranchConfig {

    private final ControlBranchBalanceConfig balanceConfig;
    private final ControlBranchCostConfig costConfig;

    public ControlBranchConfig(ControlBranchBalanceConfig balanceConfig, ControlBranchCostConfig costConfig) {
        this.balanceConfig = balanceConfig;
        this.costConfig = costConfig;
    }

    @Override
    public LabUpgrade getBaseUpgrade() {
        return LabUpgrade.SCARAB_SCANNERS;
    }

    @Override
    public List<LabUpgrade> getFirstTierUpgrades() {
        return List.of(
                LabUpgrade.KING_OF_DESERT,
                LabUpgrade.THE_GREAT_FORTRESS,
                LabUpgrade.TOWER_CREATOR
        );
    }

    @Override
    public List<LabUpgrade> getSecondTierUpgrades() {
        return List.of(
                LabUpgrade.GOLD_DIGGERS,
                LabUpgrade.FACTORY_TURRET
        );
    }

    @Override
    public LabUpgrade getSuperUpgrade() {
        return LabUpgrade.SUPER_SONIC_ROCKETS;
    }
}
