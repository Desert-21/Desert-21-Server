package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.misc.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardUtilsTest {

    @Test
    void ownsAtLeastOneLocationHappyPath() {
        var allFields = BoardUtils.generateEmptyPlain(9);
        var player = new Player();
        var playersId = "ID123";
        player.setId(playersId);
        allFields[3][3].setOwnerId(playersId);
        var locations = List.of(
                new Location(2, 3),
                new Location(2, 4),
                new Location(3, 3),
                new Location(3, 4)
        );
        var ownsAtLeastOne = BoardUtils.ownsAtLeastOneLocation(allFields, locations, player);
        assertTrue(ownsAtLeastOne);
    }

    @Test
    void ownsAtLeastOneLocationUnhappyPath() {
        var allFields = BoardUtils.generateEmptyPlain(9);
        var player = new Player();
        var playersId = "ID123";
        player.setId(playersId);
        var locations = List.of(
                new Location(2, 3),
                new Location(2, 4),
                new Location(3, 3),
                new Location(3, 4)
        );
        var ownsAtLeastOne = BoardUtils.ownsAtLeastOneLocation(allFields, locations, player);
        assertFalse(ownsAtLeastOne);
    }

    @Test
    void fieldsAtLocations() {
        var allFields = BoardUtils.generateEmptyPlain(9);
        var fields = List.of(
                allFields[2][3],
                allFields[2][4],
                allFields[3][3],
                allFields[3][4]
        );
        var locations = List.of(
                new Location(2, 3),
                new Location(2, 4),
                new Location(3, 3),
                new Location(3, 4)
        );
        var actualFields = BoardUtils.fieldsAtLocations(allFields, locations);
        for (int i = 0; i < 4; i++) {
            assertEquals(fields.get(i), actualFields.get(3-i));
        }
    }

    @Test
    void isWithinBoardBoundsHappyPath() {
        var fields = BoardUtils.generateEmptyPlain(9);
        var location = new Location(8, 4);
        var isWithinBounds = BoardUtils.isWithinBoardBounds(fields, location);
        assertTrue(isWithinBounds);
    }

    @Test
    void isWithinBoardBoundsRowExceeds() {
        var fields = BoardUtils.generateEmptyPlain(9);
        var location = new Location(10, 4);
        var isWithinBounds = BoardUtils.isWithinBoardBounds(fields, location);
        assertFalse(isWithinBounds);
    }

    @Test
    void isWithinBoardBoundsColumnExceeds() {
        var fields = BoardUtils.generateEmptyPlain(9);
        var location = new Location(4, 14);
        var isWithinBounds = BoardUtils.isWithinBoardBounds(fields, location);
        assertFalse(isWithinBounds);
    }

    @Test
    void generateEmptyPlain() {
        var fields = BoardUtils.generateEmptyPlain(9);
        assertEquals(9, fields.length);
        for (Field[] row: fields) {
            assertEquals(9, row.length);
        }
    }

//    private Field generateField() {
//        var field = new Field();
//        return field;
//    }
}