package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.models.Location;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.maciejmalewicz.Desert21.utils.LocationUtils.areNeighbours;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LocationUtilsTest {

    @Test
    void generateLocationsSquare() {
        var expectedLocations = List.of(
                new Location(1, 1),
                new Location(1, 2),
                new Location(1, 3),
                new Location(2, 1),
                new Location(2, 2),
                new Location(2, 3)
        );
        var generatedLocations = LocationUtils.generateLocationsSquare(1, 2, 1, 3);
        IntStream.range(0, 6).forEach(i -> {
            assertEquals(expectedLocations.get(i), generatedLocations.get(i));
        });
    }

    @Test
    void generateLocationsDiagonal() {
        var expectedLocations = List.of(
                new Location(1, 1),
                new Location(2, 2),
                new Location(3, 3)
        );
        var generatedLocations = LocationUtils.generateLocationsDiagonal(1, 1, 3, 3);
        IntStream.range(0, 3).forEach(i -> {
            assertEquals(expectedLocations.get(i), generatedLocations.get(i));
        });
    }

    @Test
    void generateLocationsDiagonalOppositeDirection() {
        var expectedLocations = List.of(
                new Location(1, 1),
                new Location(2, 2),
                new Location(3, 3)
        );
        var generatedLocations = LocationUtils.generateLocationsDiagonal(3, 3, 1, 1);
        IntStream.range(0, 3).forEach(i -> {
            assertEquals(expectedLocations.get(i), generatedLocations.get(i));
        });
    }

    @Test
    void get1stLevelNeighbouringLocations() {
        var baseLocation = new Location(3, 3);
        var expectedLocations = List.of(
                new Location(4, 3),
                new Location(2, 3),
                new Location(3, 2),
                new Location(3, 4)
        );
        var generatedLocations = LocationUtils.get1stLevelNeighbouringLocations(baseLocation);
        assertThat(generatedLocations, sameBeanAs(expectedLocations));
    }

    @Test
    void get2ndLevelNeighbouringLocations() {
        var baseLocation = new Location(3, 3);
        var expectedLocations = List.of(
                new Location(4, 4),
                new Location(4, 2),
                new Location(2, 4),
                new Location(2, 2),
                new Location(3, 5),
                new Location(3, 1),
                new Location(5, 3),
                new Location(1, 3)
        );
        var generatedLocations = LocationUtils.get2ndLevelNeighbouringLocations(baseLocation);
        assertThat(generatedLocations, sameBeanAs(expectedLocations));
    }

    @Test
    void getUpTo2ndLevelNeighbouringLocations() {
        var baseLocation = new Location(3, 3);
        var expectedLocations = List.of(
                new Location(4, 3),
                new Location(2, 3),
                new Location(3, 2),
                new Location(3, 4),
                new Location(4, 4),
                new Location(4, 2),
                new Location(2, 4),
                new Location(2, 2),
                new Location(3, 5),
                new Location(3, 1),
                new Location(5, 3),
                new Location(1, 3)
        );
        var generatedLocations = LocationUtils.getUpTo2ndLevelNeighbouringLocations(baseLocation);
        assertThat(generatedLocations, sameBeanAs(expectedLocations));
    }

    @Test
    void isWithinBoundsHappyPath() {
        var location = new Location(3, 3);
        var isWithinBounds = LocationUtils.isWithinBounds(location, 0, 9, 0, 9);
        assertTrue(isWithinBounds);
    }

    @Test
    void isWithinBoundsExceedingTop() {
        var location = new Location(-1, 3);
        var isWithinBounds = LocationUtils.isWithinBounds(location, 0, 9, 0, 9);
        assertFalse(isWithinBounds);
    }

    @Test
    void isWithinBoundsExceedingBottom() {
        var location = new Location(10, 3);
        var isWithinBounds = LocationUtils.isWithinBounds(location, 0, 9, 0, 9);
        assertFalse(isWithinBounds);
    }

    @Test
    void isWithinBoundsExceedingLeft() {
        var location = new Location(3, -1);
        var isWithinBounds = LocationUtils.isWithinBounds(location, 0, 9, 0, 9);
        assertFalse(isWithinBounds);
    }

    @Test
    void isWithinBoundsExceedingRight() {
        var location = new Location(3, 10);
        var isWithinBounds = LocationUtils.isWithinBounds(location, 0, 9, 0, 9);
        assertFalse(isWithinBounds);
    }

    @Test
    void areNeighboursAllCases() {
        assertTrue(areNeighbours(new Location(1, 1), new Location(1, 2)));
        assertTrue(areNeighbours(new Location(1, 1), new Location(1, 0)));
        assertTrue(areNeighbours(new Location(1, 1), new Location(0, 1)));
        assertTrue(areNeighbours(new Location(1, 1), new Location(2, 1)));

        assertFalse(areNeighbours(new Location(1, 1), new Location(1, 1)));
        assertFalse(areNeighbours(new Location(1, 1), new Location(1, 4)));
    }
}