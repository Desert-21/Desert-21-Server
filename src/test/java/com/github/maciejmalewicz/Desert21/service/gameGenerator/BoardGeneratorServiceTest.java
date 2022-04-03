package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.misc.Location;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class BoardGeneratorServiceTest {

    public static void testFieldsAtLocations(Field[][] board, List<Location> locations, Predicate<Field> predicate) {
        locations.forEach(loc -> {
            var field = board[loc.row()][loc.col()];
            assertTrue(predicate.test(field));
        });
    }

    public static void testLocationPredicateCounting(Field[][] board, List<Location> locations, Predicate<Field> predicate, int expectedCount) {
        var actualCount = locations.stream().filter(loc -> {
            var field = board[loc.row()][loc.col()];
            return predicate.test(field);
        }).count();
        assertEquals(expectedCount, actualCount);
    }

    @Test
    void generateBoard() {
        var boardConfig = new BasicBoardGeneratorConfig();
        var tested = new BoardGeneratorService(boardConfig);

        var player1 = new Player("P1", "macior123456", new ResourceSet(60, 60, 60));
        var player2 = new Player("P2", "schabina123456", new ResourceSet(60, 60, 60));
        var board = tested.generateBoard(player1, player2);

        validateBoard(boardConfig, player1, player2, board);
    }

    public static void validateBoard(BasicBoardGeneratorConfig boardConfig,
                               Player player1,
                               Player player2,
                               Field[][] board) {
        //board size
        assertEquals(boardConfig.getSize(), board.length);
        Arrays.stream(board).forEach(row -> {
            assertEquals(boardConfig.getSize(), row.length);
        });

        //players locations
        testFieldsAtLocations(board, boardConfig.getPLayer1Locations(), field -> player1.getId().equals(field.getOwnerId()));
        testFieldsAtLocations(board, boardConfig.getPLayer2Locations(), field -> player2.getId().equals(field.getOwnerId()));

        //buildings locations
        boardConfig.getBoardLocationRules().forEach(rule -> {
            testLocationPredicateCounting(board,
                    rule.availableLocations(),
                    field -> rule.buildingType().equals(field.getBuilding().getType()),
                    rule.amount()
            );
        });
    }

}