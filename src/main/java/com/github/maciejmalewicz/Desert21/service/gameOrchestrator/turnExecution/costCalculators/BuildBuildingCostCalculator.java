package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators;

import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;

public class BuildBuildingCostCalculator {

    public static int getBuildingCost(BuildingType buildingType, GameBalanceDto gameBalance) {
        try {
            return BuildingUtils.buildingTypeToConfig(buildingType, gameBalance).costAtLevel(1);
        } catch (NotAcceptableException e) {
            return -1;
        }
    }
}
