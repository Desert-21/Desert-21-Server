package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import com.github.maciejmalewicz.Desert21.models.balance.lab.ProductionBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.ProductionBranchCostConfig;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductionBranchConfig implements LabBranchConfig {

    private final ProductionBranchBalanceConfig balanceConfig;
    private final ProductionBranchCostConfig costConfig;

    public ProductionBranchConfig(ProductionBranchBalanceConfig balanceConfig, ProductionBranchCostConfig costConfig) {
        this.balanceConfig = balanceConfig;
        this.costConfig = costConfig;
    }

    @Override
    public LabUpgrade getBaseUpgrade() {
        return LabUpgrade.HOME_SWEET_HOME;
    }

    @Override
    public List<LabUpgrade> getFirstTierUpgrades() {
        return List.of(
                LabUpgrade.MORE_METAL,
                LabUpgrade.MORE_BUILDING_MATERIALS,
                LabUpgrade.MORE_ELECTRICITY
        );
    }

    @Override
    public List<LabUpgrade> getSecondTierUpgrades() {
        return List.of(
                LabUpgrade.PRODUCTION_MANAGERS,
                LabUpgrade.FACTORY_BUILDERS
        );
    }

    @Override
    public LabUpgrade getSuperUpgrade() {
        return LabUpgrade.PRODUCTION_AI;
    }
}
