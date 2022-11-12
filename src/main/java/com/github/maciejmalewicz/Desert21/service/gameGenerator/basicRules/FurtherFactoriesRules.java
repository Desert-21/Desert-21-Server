package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;
import java.util.stream.Stream;

public class FurtherFactoriesRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return List.of(
                new BoardLocationRule(getFirstPlayersMetalFactoryLocations(), BuildingType.METAL_FACTORY, 1),
                new BoardLocationRule(getFirstPlayersBMFactoryLocations(), BuildingType.BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(getFirstPlayersElectricityFactoryLocations(), BuildingType.ELECTRICITY_FACTORY, 1),
                new BoardLocationRule(getSecondPlayersMetalFactoryLocations(), BuildingType.METAL_FACTORY, 1),
                new BoardLocationRule(getSecondPlayersBMFactoryLocations(), BuildingType.BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(getSecondPlayersElectricityFactoryLocations(), BuildingType.ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> getFirstPlayersMetalFactoryLocations() {
        return LocationUtils.generateLocationsDiagonal(2, 1, 5, 4);
    }

    private List<Location> getSecondPlayersMetalFactoryLocations() {
        return LocationUtils.generateLocationsDiagonal(1, 2, 4, 5);
    }

    private List<Location> getFirstPlayersBMFactoryLocations() {
        return List.of(new Location(1, 0), new Location(6, 5));
    }

    private List<Location> getSecondPlayersBMFactoryLocations() {
        return List.of(new Location(0, 1), new Location(5, 6));
    }

    private List<Location> getFirstPlayersElectricityFactoryLocations() {
        return List.of(new Location(2, 0), new Location(6, 4));
    }

    private List<Location> getSecondPlayersElectricityFactoryLocations() {
        return List.of(new Location(0, 2), new Location(4, 6));
    }
}
