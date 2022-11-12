package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;
import java.util.stream.Stream;

public class NearbyFactoriesRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return List.of(
                new BoardLocationRule(getFirstPlayersLocations(boardSize), BuildingType.METAL_FACTORY, 1),
                new BoardLocationRule(getFirstPlayersLocations(boardSize), BuildingType.BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(getFirstPlayersElectricityLocations(), BuildingType.ELECTRICITY_FACTORY, 1),
                new BoardLocationRule(getSecondPlayersLocations(boardSize), BuildingType.METAL_FACTORY, 1),
                new BoardLocationRule(getSecondPlayersLocations(boardSize), BuildingType.BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(getSecondPlayersElectricityLocations(), BuildingType.ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> getFirstPlayersElectricityLocations() {
        return List.of(
                new Location(3, 1),
                new Location(4, 2),
                new Location(5, 3)
        );
    }

    private List<Location> getSecondPlayersElectricityLocations() {
        return List.of(
                new Location(1, 3),
                new Location(2, 4),
                new Location(3, 5)
        );
    }


    private List<Location> getFirstPlayersLocations(int boardSize) {
        return Stream.concat(
                LocationUtils.generateLocationsSquare(boardSize - 3, boardSize - 3, 0, 1).stream(),
                LocationUtils.generateLocationsSquare(boardSize - 2, boardSize - 1, 2, 2).stream()
        ).toList();
    }

    private List<Location> getSecondPlayersLocations(int boardSize) {
        return Stream.concat(
                LocationUtils.generateLocationsSquare(0, 1, boardSize - 3, boardSize - 3).stream(),
                LocationUtils.generateLocationsSquare(2, 2, boardSize - 2, boardSize - 1).stream()
        ).toList();
    }
}
