package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;

public class MiddleFactoriesRules implements RuleSupplier {
    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return List.of(
                new BoardLocationRule(getMiddleDiagonalLocations(), BuildingType.METAL_FACTORY, 1),
                new BoardLocationRule(getMiddleDiagonalLocations(), BuildingType.BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(getMiddleDiagonalLocations(), BuildingType.ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> getMiddleDiagonalLocations() {
        return LocationUtils.generateLocationsDiagonal(0, 0, 6, 6);
    }
}
