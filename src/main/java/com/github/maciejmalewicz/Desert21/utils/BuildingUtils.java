package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.BuildingConfig;

public class BuildingUtils {

    public static BuildingConfig buildingTypeToConfig(BuildingType type, GameBalanceDto gameBalance) throws NotAcceptableException {
        var buildings = gameBalance.buildings();
        return switch (type) {
            case TOWER -> buildings.tower();
            case HOME_BASE -> buildings.homeBase();
            case ROCKET_LAUNCHER -> buildings.rocketLauncher();
            case METAL_FACTORY, BUILDING_MATERIALS_FACTORY, ELECTRICITY_FACTORY -> buildings.factory();
            case EMPTY_FIELD -> throw new NotAcceptableException("Empty field is does not have a config!");
        };
    }
}
