package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyLeavingEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArmyLeavingExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private ArmyLeavingExecutor tested;

    @BeforeEach
    void setup() {
        var player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        context = new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                new Game(
                        List.of(
                                player,
                                new Player("BB",
                                        "schabina123456",
                                        new ResourceSet(60, 60, 60))),
                        new Field[9][9],
                        new StateManager(
                                GameState.AWAITING,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[0][0].setArmy(new Army(20, 3, 14));
    }

    @Test
    void executeHappyPath() throws NotAcceptableException {
        var results = tested.execute(
                List.of(new ArmyLeavingEvent(new Location(0, 0), new Army(10, 1, 4))),
                context
        );
        var newArmy = context.game().getFields()[0][0].getArmy();
        assertEquals(new Army(10, 2, 10), newArmy);
        assertEquals(new ArrayList<>(), results.results());
    }

    @Test
    void executeShouldThrowErrorOnNotFoundField() {
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.execute(
                    List.of(
                            new ArmyLeavingEvent(new Location(0, 0), new Army(10, 1, 4)),
                            new ArmyLeavingEvent(new Location(-1, 99), new Army(10, 1, 4))
                    ),
                    context
            );
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }
}