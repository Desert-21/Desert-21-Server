package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.*;

public class PlayersFactoriesRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return List.of(
                new BoardLocationRule(firstPlayersLocations(boardSize), METAL_FACTORY, 1),
                new BoardLocationRule(firstPlayersLocations(boardSize), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(firstPlayersLocations(boardSize), ELECTRICITY_FACTORY, 1),
                new BoardLocationRule(secondPlayersLocations(boardSize), METAL_FACTORY, 1),
                new BoardLocationRule(secondPlayersLocations(boardSize), BUILDING_MATERIALS_FACTORY, 1),
                new BoardLocationRule(secondPlayersLocations(boardSize), ELECTRICITY_FACTORY, 1)
        );
    }

    private List<Location> firstPlayersLocations(int boardSize) {
        return LocationUtils.generateLocationsSquare(boardSize - 2, boardSize - 1, 0, 1).stream()
                .filter(l -> !l.equals(new Location(boardSize - 2, 1)))
                .collect(Collectors.toList());
    }

    private List<Location> secondPlayersLocations(int boardSize) {
        return LocationUtils.generateLocationsSquare(0, 1, boardSize - 2, boardSize - 1).stream()
                .filter(l -> !l.equals(new Location(1, boardSize - 2)))
                .collect(Collectors.toList());
    }
}
