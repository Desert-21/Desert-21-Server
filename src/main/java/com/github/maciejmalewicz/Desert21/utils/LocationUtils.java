package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.models.Location;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocationUtils {

    public static List<Location> generateLocationsSquare(int yStart, int yEnd, int xStart, int xEnd) {
        return IntStream.rangeClosed(yStart, yEnd)
                .mapToObj(y -> Pair.of(y, IntStream.rangeClosed(xStart, xEnd)))
                .flatMap(pair -> pair.getSecond().mapToObj(x -> new Location(pair.getFirst(), x)))
                .collect(Collectors.toList());
    }

    public static List<Location> generateLocationsDiagonal(int ay, int ax, int by, int bx) {
        if (ax > bx) {
            var temp = ax;
            ax = bx;
            bx = temp;
        }
        if (ay > by) {
            var temp = ay;
            ay = by;
            by = temp;
        }
        var yDiff = by - ay;
        var yList = IntStream.rangeClosed(ay, by).boxed().toList();
        var xList = IntStream.rangeClosed(ax, bx).boxed().toList();
        return IntStream.rangeClosed(0, yDiff)
                .mapToObj(i -> new Location(yList.get(i), xList.get(i)))
                .collect(Collectors.toList());
    }

    public static List<Location> get1stLevelNeighbouringLocations(Location location) {
        return List.of(
                new Location(location.row() + 1, location.col()),
                new Location(location.row() - 1, location.col()),
                new Location(location.row(), location.col() - 1),
                new Location(location.row(), location.col() + 1)
        );
    }

    public static List<Location> get2ndLevelNeighbouringLocations(Location location) {
        return List.of(
                new Location(location.row() + 1, location.col() + 1),
                new Location(location.row() + 1, location.col() - 1),
                new Location(location.row() - 1, location.col() + 1),
                new Location(location.row() - 1, location.col() - 1)
        );
    }

    public static List<Location> getUpTo2ndLevelNeighbouringLocations(Location location) {
        return generateLocationsSquare(
                location.row() - 1,
                location.row() + 1,
                location.col() - 1,
                location.col() + 1
        ).stream()
                .filter(l -> !l.equals(location))
                .collect(Collectors.toList());
    }

    public static boolean isWithinBounds(Location location, int yMin, int yMax, int xMin, int xMax) {
        return location.row() >= yMin
                && location.row() <= yMax
                && location.col() >= xMin
                && location.col() <= xMax;
    }

    public static boolean areNeighbours(Location l1, Location l2) {
        var yDiff = Math.abs(l1.row() - l2.row());
        var xDiff = Math.abs(l1.col() - l2.col());
        var totalDiff = xDiff + yDiff;
        return totalDiff == 1;
    }
}
