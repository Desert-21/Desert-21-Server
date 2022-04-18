package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators.FieldOwnershipValidator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuildingUpgradeExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private BuildingUpgradeExecutor tested;

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
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
    }

    @Test
    void executeHappyPath() throws NotAcceptableException {
        var events = List.of(
                new BuildingUpgradeEvent(new Location(0, 0)),
                new BuildingUpgradeEvent(new Location(0, 1))
        );
        context = tested.execute(events, context);
        assertEquals(2, context.game().getFields()[0][0].getBuilding().getLevel());
        assertEquals(2, context.game().getFields()[0][1].getBuilding().getLevel());
    }

    @Test
    void executeUnhappyPathShouldNotBreak() throws NotAcceptableException {
        var events = List.of(
                new BuildingUpgradeEvent(new Location(0, 0)),
                new BuildingUpgradeEvent(new Location(0, 1)),
                new BuildingUpgradeEvent(new Location(12, 12))
        );
        context = tested.execute(events, context);
        assertEquals(2, context.game().getFields()[0][0].getBuilding().getLevel());
        assertEquals(2, context.game().getFields()[0][1].getBuilding().getLevel());
    }
}