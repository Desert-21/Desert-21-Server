package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicGameTimerTest {

    private Game game;

    @BeforeEach
    void setup() {
        var player1 = new Player("AA", "macior123456", new ResourceSet(60, 60, 60));
        var player2 = new Player("BB", "macior123456", new ResourceSet(60, 60, 60));
        game = new Game(
                List.of(player1, player2),
                BoardUtils.generateEmptyPlain(9),
                new StateManager(
                        GameState.AWAITING,
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game.getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        game.getFields()[0][1] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        game.getFields()[1][1] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "AA");
        game.getFields()[1][0] = new Field(new Building(BuildingType.METAL_FACTORY), "AA");

        game.getFields()[7][7] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "BB");
        game.getFields()[7][8] = new Field(new Building(BuildingType.HOME_BASE), "BB");
        game.getFields()[8][7] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "BB");
        game.getFields()[8][8] = new Field(new Building(BuildingType.METAL_FACTORY), "BB");
    }

    @Test
    void getMoveTime() {
        var timer = new BasicGameTimer();
        assertEquals(28_000, timer.getMoveTime(game));
    }
}