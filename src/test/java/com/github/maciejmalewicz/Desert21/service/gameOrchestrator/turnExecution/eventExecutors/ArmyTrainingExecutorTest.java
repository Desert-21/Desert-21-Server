package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.UnitType;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.ArmyTrainingEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArmyTrainingExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private ArmyTrainingExecutor tested;

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
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.TOWER), "AA");
    }

    @Test
    void executeHappyPath() throws NotAcceptableException {
        var events = List.of(
                new ArmyTrainingEvent(0, new Location(0, 0), UnitType.DROID, 10),
                new ArmyTrainingEvent(0, new Location(0, 0), UnitType.TANK, 4)
        );
        var executionResults = tested.execute(events, context);

        var updatedFields = executionResults.context().game().getFields();
        var army = updatedFields[0][0].getArmy();
        assertEquals(new Army(10, 4, 0), army);

        var eventResults = executionResults.results();
        assertEquals(2, eventResults.size());
        assertEquals(new ArmyTrainingEventResult(new Location(0, 0), UnitType.DROID, 10), eventResults.get(0));
        assertEquals(new ArmyTrainingEventResult(new Location(0, 0), UnitType.TANK, 4), eventResults.get(1));
    }

    @Test
    void executeUnhappyPath() {
        var events = List.of(
                new ArmyTrainingEvent(0, new Location(-1, 0), UnitType.DROID, 10),
                new ArmyTrainingEvent(0, new Location(0, 0), UnitType.TANK, 4)
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.execute(events, context);
        });
        assertNotNull(exception.getMessage());
    }
}