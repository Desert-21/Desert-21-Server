package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;

public class UpgradeBuildingCostCalculator {

    /**
     * Calculates how much does it cost to upgrade specific building
     * @param building the building at the current level, NOT the next one
     * @param gameBalanceDto game balance object
     * @return amount of BM to upgrade a building
     */
    public static int getUpgradeCost(Building building, GameBalanceDto gameBalanceDto) {
        try {
            var buildingConfig = BuildingUtils.buildingTypeToConfig(building.getType(), gameBalanceDto);
            return buildingConfig.costAtLevel(building.getLevel() + 1);
        } catch (NotAcceptableException notAcceptableException) {
            return -1;
        }
    }
}
