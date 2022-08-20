package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GameEndCheckingServiceTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private Game game;

    @Autowired
    private GameEndCheckingService tested;

    @BeforeEach
    void setup() {
        var player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        game = new Game(
                        List.of(
                                player,
                                new Player("BB",
                                        "schabina123456",
                                        new ResourceSet(60, 60, 60))),
                        BoardUtils.generateEmptyPlain(7),
                        new StateManager(
                                GameState.AWAITING,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                );
    }

    @Test
    void checkIfGameHasEndedWhenHasNotEnded() {
        game.getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        game.getFields()[0][1] = new Field(new Building(BuildingType.METAL_FACTORY), "AA");
        game.getFields()[5][6] = new Field(new Building(BuildingType.METAL_FACTORY), "BB");
        game.getFields()[6][6] = new Field(new Building(BuildingType.HOME_BASE), "BB");
        var result = tested.checkIfGameHasEnded(game);
        assertTrue(result.isEmpty());
    }

    @Test
    void checkIfGameHasEndedWhenHasActuallyEnded() {
        game.getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        game.getFields()[0][1] = new Field(new Building(BuildingType.METAL_FACTORY), "AA");
        game.getFields()[5][6] = new Field(new Building(BuildingType.METAL_FACTORY), "BB");
        game.getFields()[6][6] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        var result = tested.checkIfGameHasEnded(game);
        assertTrue(result.isPresent());
        assertThat("AA", sameBeanAs(result.get()));
    }
}