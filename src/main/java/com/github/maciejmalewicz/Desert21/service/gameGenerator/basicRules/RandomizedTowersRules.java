package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;

public class RandomizedTowersRules implements RuleSupplier {
    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return List.of(
                new BoardLocationRule(getFirstPlayersCloserLocations(), BuildingType.TOWER, 1),
                new BoardLocationRule(getFirstPlayersFurtherLocations(), BuildingType.TOWER, 1),
                new BoardLocationRule(getSecondPlayersCloserLocations(), BuildingType.TOWER, 1),
                new BoardLocationRule(getSecondPlayersFurtherLocations(), BuildingType.TOWER, 1)
        );
    }

    private List<Location> getFirstPlayersFurtherLocations() {
        return LocationUtils.generateLocationsDiagonal(2, 1, 5, 4);
    }

    private List<Location> getSecondPlayersFurtherLocations() {
        return LocationUtils.generateLocationsDiagonal(1, 2, 4, 5);
    }

    private List<Location> getFirstPlayersCloserLocations() {
        return List.of(
                new Location(3, 1),
                new Location(4, 2),
                new Location(5, 3)
        );
    }

    private List<Location> getSecondPlayersCloserLocations() {
        return List.of(
                new Location(1, 3),
                new Location(2, 4),
                new Location(3, 5)
        );
    }
}
