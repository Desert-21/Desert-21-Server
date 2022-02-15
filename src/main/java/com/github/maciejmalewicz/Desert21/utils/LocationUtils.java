package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.misc.Location;
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
}
