package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.BuildingUpgradedNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators.FieldOwnershipValidator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BuildingUpgradeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.BUILDING_UPGRADED_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuildingUpgradeExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private BuildingUpgradeExecutor tested;
    private List<EventResult> results;

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
        var eventExecutionResult = tested.execute(events, context);
        context = eventExecutionResult.context();
        var eventResults = eventExecutionResult.results();

        assertEquals(2, context.game().getFields()[0][0].getBuilding().getLevel());
        assertEquals(2, context.game().getFields()[0][1].getBuilding().getLevel());
        var expectedResults = new ArrayList<EventResult>();
        expectedResults.add(new BuildingUpgradeEventResult(1, 2, new Location(0, 0)));
        expectedResults.add(new BuildingUpgradeEventResult(1, 2, new Location(0, 1)));
        assertEquals(expectedResults, eventResults);

        var firstNotification = eventResults.get(0).forBoth().get(0);
        assertEquals(BUILDING_UPGRADED_NOTIFICATION, firstNotification.type());
        var notificationContent = (BuildingUpgradedNotification) firstNotification.content();
        assertEquals(1, notificationContent.getFromLevel());
        assertEquals(2, notificationContent.getToLevel());
        assertEquals(new Location(0, 0), notificationContent.getLocation());
    }

    @Test
    void executeUnhappyPathShouldNotBreak() throws NotAcceptableException {
        var events = List.of(
                new BuildingUpgradeEvent(new Location(0, 0)),
                new BuildingUpgradeEvent(new Location(0, 1)),
                new BuildingUpgradeEvent(new Location(12, 12))
        );
        var eventExecutionResult = tested.execute(events, context);
        context = eventExecutionResult.context();
        var eventResults = eventExecutionResult.results();

        assertEquals(2, context.game().getFields()[0][0].getBuilding().getLevel());
        assertEquals(2, context.game().getFields()[0][1].getBuilding().getLevel());
        var expectedResults = new ArrayList<EventResult>();
        expectedResults.add(new BuildingUpgradeEventResult(1, 2, new Location(0, 0)));
        expectedResults.add(new BuildingUpgradeEventResult(1, 2, new Location(0, 1)));
        assertEquals(expectedResults, eventResults);
    }
}