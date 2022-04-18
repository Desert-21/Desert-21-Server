package com.github.maciejmalewicz.Desert21.service.gameSnapshot;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArmySnapshotProcessingServiceTest {

    @Autowired
    private ArmySnapshotProcessingService tested;

    @Autowired
    private GeneralConfig generalConfig;

    private Player player;
    private Player enemy;
    private Field[][] fields;

    private Comparator<Army> armyComparator;

    @BeforeEach
    void setup() {
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        enemy = new Player("BB",
                "schabina123456",
                new ResourceSet(60, 60, 60));

        armyComparator = (army1, army2) -> {
            var sum1 = sumArmy(army1);
            var sum2 = sumArmy(army2);
            return sum1 - sum2;
        };

        setupBasicFields();
    }

    void setupBasicFields() {
        fields = new Field[3][3];
        IntStream.range(0, fields.length).forEach(i -> {
            fields[i] = new Field[3];
        });

        //all fields occupied by enemy by default and having basic army
        IntStream.range(0, fields.length).forEach(i -> {
            IntStream.range(0, fields[i].length).forEach(j -> {
                fields[i][j] = new Field(new Building(BuildingType.EMPTY_FIELD));
                fields[i][j].setOwnerId(enemy.getId());
                fields[i][j].setArmy(new Army(100, 100, 100));
            });
        });

        //one field in the corner occupied by player
        fields[0][0].setOwnerId(player.getId());
    }

    int sumArmy(Army army) {
        return army.getDroids() + army.getTanks() + army.getCannons();
    }

    @Test
    void snapshotArmyOwned() {
        var army = tested.snapshotArmy(player, fields, new Location(0, 0));
        assertEquals(new Army(100, 100, 100), army);
    }

    @Test
    void snapshotEnemiesCloseEmptyArmy() {
        fields[1][0].setArmy(new Army(0, 0, 0));
        fields[1][1].setArmy(new Army(0, 0, 0));
        var army1 = tested.snapshotArmy(player, fields, new Location(1, 0));
        var army2 = tested.snapshotArmy(player, fields, new Location(1, 1));

        assertEquals(new Army(0, 0, 0), army1);
        assertEquals(new Army(0, 0, 0), army2);
    }

    @Test
    void snapshotEnemiesBehindTheFogArmy() {
        var army1 = tested.snapshotArmy(player, fields, new Location(1, 2));
        var army2 = tested.snapshotArmy(player, fields, new Location(2, 1));
        var army3 = tested.snapshotArmy(player, fields, new Location(2, 2));
        assertNull(army1);
        assertNull(army2);
        assertNull(army3);
    }

    @Test
    void snapshotOpponentsArmyNextToPlayer() {
        var results = IntStream.range(0, 10000)
                .mapToObj(i ->
                        tested.snapshotArmy(player, fields, new Location(1, 0))
                ).toList();
        var min = results.stream().min(armyComparator).orElseThrow();
        var max = results.stream().max(armyComparator).orElseThrow();

        var bias = ((double) generalConfig.getFogOfWar1()) / 100.0;
        var minSum = (300.0 / (1 + bias)) - 1;
        var maxSum = (300.0 / (1 - bias)) + 1;

        assertTrue(sumArmy(min) > minSum);
        assertTrue(sumArmy(max) < maxSum);
    }

    @Test
    void snapshotOpponentsArmyNextToNextToPlayer() {
        var results = IntStream.range(0, 10000)
                .mapToObj(i ->
                        tested.snapshotArmy(player, fields, new Location(1, 1))
                ).toList();
        var min = results.stream().min(armyComparator).orElseThrow();
        var max = results.stream().max(armyComparator).orElseThrow();

        var bias = ((double) generalConfig.getFogOfWar2()) / 100.0;
        var minSum = (300.0 / (1 + bias)) - 1;
        var maxSum = (300.0 / (1 - bias)) + 1;

        assertTrue(sumArmy(min) > minSum);
        assertTrue(sumArmy(max) < maxSum);
    }
}