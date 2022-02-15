package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.misc.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.misc.BuildingType.*;

public class PlayersFactoriesRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules() {
        return List.of(
                new BoardLocationRule(firstPlayersLocations(), METAL_FACTORY, 1),
                new BoardLocationRule(firstPlayersLocations(), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(firstPlayersLocations(), ELECTRICITY_FACTORY, 1),
                new BoardLocationRule(secondPlayersLocations(), METAL_FACTORY, 1),
                new BoardLocationRule(secondPlayersLocations(), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(secondPlayersLocations(), ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> firstPlayersLocations() {
        return LocationUtils.generateLocationsSquare(9, 10, 0, 1).stream()
                .filter(l -> !l.equals(new Location(9, 1)))
                .collect(Collectors.toList());
    }

    private List<Location> secondPlayersLocations() {
        return LocationUtils.generateLocationsSquare(0, 1, 9, 10).stream()
                .filter(l -> !l.equals(new Location(1, 9)))
                .collect(Collectors.toList());
    }
}
