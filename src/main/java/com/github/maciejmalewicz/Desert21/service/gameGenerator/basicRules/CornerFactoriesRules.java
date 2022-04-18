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
    public List<BoardLocationRule> getRules() {
        return List.of(
                new BoardLocationRule(northWestLocations(), METAL_FACTORY, 1),
                new BoardLocationRule(northWestLocations(), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(northWestLocations(), ELECTRICITY_FACTORY, 1),
                new BoardLocationRule(southEastLocations(), METAL_FACTORY, 1),
                new BoardLocationRule(southEastLocations(), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(southEastLocations(), ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> northWestLocations() {
        return LocationUtils.generateLocationsSquare(0, 1, 0, 1).stream()
                .filter(l -> !l.equals(new Location(1, 1)))
                .collect(Collectors.toList());
    }

    private List<Location> southEastLocations() {
        return LocationUtils.generateLocationsSquare(9, 10, 9, 10).stream()
                .filter(l -> !l.equals(new Location(9, 9)))
                .collect(Collectors.toList());
    }
}
