package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.BuildingType;

import java.util.List;

public class RocketCostCalculator {

    public static int calculateRocketCost(GameBalanceDto gameBalanceDto, Player player, List<Field> ownedFields) {
        var config = gameBalanceDto.general();

        if (player.isNextRocketFree()) {
            return 0;
        }

        var rocketStrikesPerformed = player.getRocketStrikesDone();
        var baseCost = config.getBaseRocketStrikePrice() + rocketStrikesPerformed * config.getRocketStrikePricePerUsage();
        var rocketLaunchersAmount = ownedFields.stream()
                .filter(RocketCostCalculator::isRocketLauncher)
                .count();
        var discountRatio = Math.pow(0.5, rocketLaunchersAmount - 1);
        return (int) Math.round(baseCost * discountRatio);
    }

    public static boolean isRocketLauncher(Field field) {
        if (field.getBuilding() == null) {
            return false;
        }
        var buildingType = field.getBuilding().getType();
        return buildingType.equals(BuildingType.ROCKET_LAUNCHER);
    }
}
