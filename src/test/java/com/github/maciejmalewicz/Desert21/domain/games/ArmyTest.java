package com.github.maciejmalewicz.Desert21.domain.games;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArmyTest {

    @Test
    void isEmpty() {
        var notEmptyArmy = new Army(10, 0, 0);
        assertFalse(notEmptyArmy.isEmpty());

        var emptyArmy = new Army(0, 0, 0);
        assertTrue(emptyArmy.isEmpty());
    }

    @Test
    void combineWith() {
        var army1 = new Army(10, 2, 5);
        var army2 = new Army(20, 5, 3);
        var combined = army1.combineWith(army2);
        assertEquals(new Army(30, 7, 8), combined);
    }
}