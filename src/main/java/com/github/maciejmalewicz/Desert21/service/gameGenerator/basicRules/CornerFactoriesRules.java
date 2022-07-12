package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.*;

public class CornerFactoriesRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return List.of(
                new BoardLocationRule(northWestLocations(), METAL_FACTORY, 1),
                new BoardLocationRule(northWestLocations(), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(northWestLocations(), ELECTRICITY_FACTORY, 1),
                new BoardLocationRule(southEastLocations(boardSize), METAL_FACTORY, 1),
                new BoardLocationRule(southEastLocations(boardSize), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(southEastLocations(boardSize), ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> northWestLocations() {
        return LocationUtils.generateLocationsSquare(0, 1, 0, 1).stream()
                .filter(l -> !l.equals(new Location(1, 1)))
                .collect(Collectors.toList());
    }

    private List<Location> southEastLocations(int boardSize) {
        return LocationUtils.generateLocationsSquare(boardSize - 2, boardSize - 1, boardSize - 2, boardSize - 1).stream()
                .filter(l -> !l.equals(new Location(boardSize - 2, boardSize - 2)))
                .collect(Collectors.toList());
    }
}
