package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.misc.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;
import com.google.common.collect.Streams;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.misc.BuildingType.*;

public class PartialSquaresRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules() {
        return Streams.concat(
                getSingleSquareSetup(northWest()).stream(),
                getSingleSquareSetup(northEast()).stream(),
                getSingleSquareSetup(southWest()).stream(),
                getSingleSquareSetup(southEast()).stream()
        ).collect(Collectors.toList());
    }

    private List<BoardLocationRule> getSingleSquareSetup(List<Location> locations) {
        return List.of(
                new BoardLocationRule(locations, METAL_FACTORY, 2),
                new BoardLocationRule(locations, BUILDING_MATERIALS_FACTORY, 2),
                new BoardLocationRule(locations, ELECTRICITY_FACTORY, 2),
                new BoardLocationRule(locations, TOWER, 2)
        );
    }

    private List<Location> northWest() {
        var cornerSquares = LocationUtils.generateLocationsSquare(0, 1, 0, 1);
        return LocationUtils.generateLocationsSquare(0, 4, 0, 4).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }

    private List<Location> northEast() {
        var cornerSquares = LocationUtils.generateLocationsSquare(0, 1, 9, 10);
        return LocationUtils.generateLocationsSquare(0, 4, 6, 10).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }

    private List<Location> southWest() {
        var cornerSquares = LocationUtils.generateLocationsSquare(9, 10, 0, 1);
        return LocationUtils.generateLocationsSquare(6, 10, 0, 4).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }

    private List<Location> southEast() {
        var cornerSquares = LocationUtils.generateLocationsSquare(9, 10, 9, 10);
        return LocationUtils.generateLocationsSquare(6, 10, 6, 10).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }
}
