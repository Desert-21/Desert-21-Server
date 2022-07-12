package com.github.maciejmalewicz.Desert21.service.gameGenerator.basicRules;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.BoardLocationRule;
import com.github.maciejmalewicz.Desert21.service.gameGenerator.RuleSupplier;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;
import com.google.common.collect.Streams;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.models.BuildingType.*;

public class PartialSquaresRules implements RuleSupplier {

    @Override
    public List<BoardLocationRule> getRules(int boardSize) {
        return Streams.concat(
                getSingleSquareSetup(northWest(boardSize)).stream(),
                getSingleSquareSetup(northEast(boardSize)).stream(),
                getSingleSquareSetup(southWest(boardSize)).stream(),
                getSingleSquareSetup(southEast(boardSize)).stream()
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

    private List<Location> northWest(int boardSize) {
        var middleMinusOne = (boardSize / 2) - 1;
        var cornerSquares = LocationUtils.generateLocationsSquare(0, 1, 0, 1);
        return LocationUtils.generateLocationsSquare(0, middleMinusOne, 0, middleMinusOne).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }

    private List<Location> northEast(int boardSize) {
        var middleMinusOne = (boardSize / 2) - 1;
        var middlePlusOne = (boardSize / 2) + 1;
        var almostLast = boardSize - 2;
        var last = boardSize - 1;
        var cornerSquares = LocationUtils.generateLocationsSquare(0, 1, almostLast, last);
        return LocationUtils.generateLocationsSquare(0, middleMinusOne, middlePlusOne, last).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }

    private List<Location> southWest(int boardSize) {
        var middleMinusOne = (boardSize / 2) - 1;
        var middlePlusOne = (boardSize / 2) + 1;
        var almostLast = boardSize - 2;
        var last = boardSize - 1;
        var cornerSquares = LocationUtils.generateLocationsSquare(almostLast, last, 0, 1);
        return LocationUtils.generateLocationsSquare(middlePlusOne, last, 0, middleMinusOne).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }

    private List<Location> southEast(int boardSize) {
        var middlePlusOne = (boardSize / 2) + 1;
        var almostLast = boardSize - 2;
        var last = boardSize - 1;
        var cornerSquares = LocationUtils.generateLocationsSquare(almostLast, last, almostLast, last);
        return LocationUtils.generateLocationsSquare(middlePlusOne, last, middlePlusOne, last).stream()
                .filter(loc -> !cornerSquares.contains(loc))
                .collect(Collectors.toList());
    }
}
