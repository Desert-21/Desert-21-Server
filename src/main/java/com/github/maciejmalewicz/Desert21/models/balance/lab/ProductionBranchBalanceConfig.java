package com.github.maciejmalewicz.Desert21.models.balance.lab;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductionBranchBalanceConfig {
    private double homeSweetHomeProductionBonus;

    private double moreMetalProductionRelativeBonus;
    private double moreBuildingMaterialsProductionRelativeBonus;
    private double moreElectricityProductionRelativeBonus;
    private int moreMetalProductionStaticBonus;
    private int moreBuildingMaterialsProductionStaticBonus;
    private int moreElectricityProductionStaticBonus;

    private double productionManagersProductionBonus;
    private int factoryBuildingMaxFactoriesBuilt;

    private int productionAiIncreasePerTurn;
}
