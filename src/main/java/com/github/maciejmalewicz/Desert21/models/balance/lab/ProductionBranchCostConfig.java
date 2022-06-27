package com.github.maciejmalewicz.Desert21.models.balance.lab;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductionBranchCostConfig {
    private int homeSweetHome;

    private int moreMetal;
    private int moreBuildingMaterials;
    private int moreElectricity;

    private int productionManagers;
    private int factoryBuilders;

    private int productionAi;
}
