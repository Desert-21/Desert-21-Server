package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UpgradeActionTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

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
                                GameState.WAITING_TO_START,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
    }

    @Test
    void getActionValidatablesLocationOutOfBounds() {
        var upgradeAction = new UpgradeAction(new Location(20, 20));
        var exception = assertThrows(NotAcceptableException.class, () -> {
            upgradeAction.getActionValidatables(context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }

    @Test
    void getActionValidatablesTryingToUpgradeEmptyField() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        var upgradeAction = new UpgradeAction(new Location(0, 0));
        var exception = assertThrows(NotAcceptableException.class, () -> {
            upgradeAction.getActionValidatables(context);
        });
        assertEquals("Empty field is does not have a config!", exception.getMessage());
    }

    @Test
    void getActionValidatablesHappyPath() throws NotAcceptableException {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        var upgradeAction = new UpgradeAction(new Location(0, 0));
        var validatables = upgradeAction.getActionValidatables(context);
        var expectedValidatables = List.of(
                new LocationBoundsValidatable(new Location(0, 0)),
                new FieldOwnershipValidatable(context.game().getFields()[0][0], context.player()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][0].getBuilding()),
                new SingleUpgradePerLocationValidatable(new Location(0, 0)),
                new CostValidatable(new ResourceSet(0, 40, 0)),
                new HasSufficientLabForBuildingUpgradeValidatable(new Location(0, 0))
        );
        assertThat(expectedValidatables, sameBeanAs(validatables));
    }

    @Test
    void getEventExecutablesLocationOutOfBounds() {
        var upgradeAction = new UpgradeAction(new Location(12, 12));
        var exception = assertThrows(NotAcceptableException.class, () -> {
            upgradeAction.getEventExecutables(context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }

    @Test
    void getEventExecutablesTryingToUpgradeEmptyField() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        var upgradeAction = new UpgradeAction(new Location(0, 0));
        var exception = assertThrows(NotAcceptableException.class, () -> {
            upgradeAction.getEventExecutables(context);
        });
        assertEquals("Empty field is does not have a config!", exception.getMessage());
    }

    @Test
    void getEventExecutablesHappyPath() throws NotAcceptableException {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        var upgradeAction = new UpgradeAction(new Location(0, 0));
        var events = upgradeAction.getEventExecutables(context);
        assertEquals(2, events.size());
        assertEquals(new PaymentEvent(new ResourceSet(0, 40, 0)), events.get(0));
        assertEquals(new BuildingUpgradeEvent(new Location(0, 0)), events.get(1));
    }
}