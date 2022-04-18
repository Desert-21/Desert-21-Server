package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ActionValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.UpgradeAction;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayersActionsValidatingServiceTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    @Autowired
    private PlayersActionsValidatingService tested;

    private TurnExecutionContext context;

    void setupContext() {
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
                                GameState.WAITING_TO_START,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
    }

    @BeforeEach
    void setup() {
        setupContext();
    }

    @Test
    void validatePlayersActionsHappyPath() {
        var action = new UpgradeAction(new Location(0, 0));
        var actions = new ArrayList<Action>();
        actions.add(action);
        var validationResult = tested.validatePlayersActions(actions, context);
        assertTrue(validationResult);
    }

    @Test
    void validatePlayersActionsFailingValidatorOnCost() {
        context.player().setResources(new ResourceSet(0, 0, 0));
        var action = new UpgradeAction(new Location(0, 0));
        var actions = new ArrayList<Action>();
        actions.add(action);
        var validationResult = tested.validatePlayersActions(actions, context);
        assertFalse(validationResult);
    }

    @Test
    void validatePlayersActionsFailingActionThrowingExceptionFieldOutsideOfBounds() {
        var action = new UpgradeAction(new Location(12, 12));
        var actions = new ArrayList<Action>();
        actions.add(action);
        var validationResult = tested.validatePlayersActions(actions, context);
        assertFalse(validationResult);
    }
}