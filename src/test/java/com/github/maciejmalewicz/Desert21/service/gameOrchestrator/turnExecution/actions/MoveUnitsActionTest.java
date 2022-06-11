package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyEnteringEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyLeavingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MoveUnitsActionTest {

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
                        BoardUtils.generateEmptyPlain(9),
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
    void getEventExecutables() throws NotAcceptableException {
        var action = new MoveUnitsAction(
                new Location(0, 0),
                new Location(1, 1),
                List.of(new Location(0, 0), new Location(1, 0), new Location(1, 1)),
                new Army(10, 2, 4)
        );
        var events = action.getEventExecutables(context);
        assertEquals(2, events.size());
        var fromEvent = (ArmyLeavingEvent) events.get(0);
        var toEvent = (ArmyEnteringEvent) events.get(1);
        assertEquals(new Location(0, 0), fromEvent.getLocation());
        assertEquals(new Location(1, 1), toEvent.getLocation());
        assertEquals(new Army(10, 2, 4), fromEvent.getArmy());
        assertEquals(new Army(10, 2, 4), toEvent.getArmy());
    }

    @Test
    void getActionValidatablesShouldThrowErrorOnFromLocationOutOfBounds() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.TOWER), "AA");
        var action = new MoveUnitsAction(
                new Location(99, 0),
                new Location(1, 1),
                List.of(new Location(99, 1), new Location(0, 0), new Location(1, 1)),
                new Army(10, 2, 4)
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
           action.getActionValidatables(context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }

    @Test //handled by the validatables later
    void getActionValidatablesShouldNotBreakOnLocationOutOfBounds() throws NotAcceptableException {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.TOWER), "AA");
        var action = new MoveUnitsAction(
                new Location(0, 0),
                new Location(1, 1),
                List.of(new Location(0, 0), new Location(99, 1), new Location(1, 1)),
                new Army(10, 2, 4)
        );
        var validatables = action.getActionValidatables(context);
        var fieldOwnershipValidatables = validatables.stream()
                .filter(a -> a instanceof FieldOwnershipValidatable)
                .map(FieldOwnershipValidatable.class::cast)
                .toList();


        assertEquals(context.game().getFields()[0][0], fieldOwnershipValidatables.get(0).field());
        assertEquals(context.player(), fieldOwnershipValidatables.get(0).player());
        assertNull(fieldOwnershipValidatables.get(1).field());
        assertEquals(context.player(), fieldOwnershipValidatables.get(1).player());
        assertEquals(context.game().getFields()[1][1], fieldOwnershipValidatables.get(2).field());
        assertEquals(context.player(), fieldOwnershipValidatables.get(2).player());
    }

    @Test
    void getActionValidatablesHappyPath() throws NotAcceptableException {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.TOWER), "AA");
        var action = new MoveUnitsAction(
                new Location(0, 0),
                new Location(1, 1),
                List.of(new Location(0, 0), new Location(0, 1), new Location(1, 1)),
                new Army(10, 2, 4)
        );
        var validatables = action.getActionValidatables(context);
        assertEquals(10, validatables.size());

        var pathConvergenceValidatable = validatables.stream()
                .filter(a -> a instanceof PathFromAndToConvergenceValidatable)
                .map(PathFromAndToConvergenceValidatable.class::cast)
                .findAny()
                .orElseThrow();
        assertEquals(new Location(0, 0), pathConvergenceValidatable.from());
        assertEquals(new Location(1, 1), pathConvergenceValidatable.to());
        assertEquals(new Location(0, 0), pathConvergenceValidatable.path().get(0));
        assertEquals(new Location(0, 1), pathConvergenceValidatable.path().get(1));
        assertEquals(new Location(1, 1), pathConvergenceValidatable.path().get(2));

        var pathContinuityValidatable = validatables.stream()
                .filter(a -> a instanceof PathContinuityValidatable)
                .map(PathContinuityValidatable.class::cast)
                .findAny()
                .orElseThrow();
        assertEquals(new Location(0, 0), pathContinuityValidatable.path().get(0));
        assertEquals(new Location(0, 1), pathContinuityValidatable.path().get(1));
        assertEquals(new Location(1, 1), pathContinuityValidatable.path().get(2));

        var pathLengthValidatable = validatables.stream()
                .filter(a -> a instanceof PathLengthValidatable)
                .map(PathLengthValidatable.class::cast)
                .findAny()
                .orElseThrow();
        assertEquals(new Army(10, 2, 4), pathLengthValidatable.army());
        assertEquals(new Location(0, 0), pathLengthValidatable.path().get(0));
        assertEquals(new Location(0, 1), pathLengthValidatable.path().get(1));
        assertEquals(new Location(1, 1), pathLengthValidatable.path().get(2));

        var locationBoundsValidatables = validatables.stream()
                .filter(a -> a instanceof LocationBoundsValidatable)
                .map(LocationBoundsValidatable.class::cast)
                .toList();
        assertEquals(3, locationBoundsValidatables.size());
        assertEquals(new Location(0, 0), locationBoundsValidatables.get(0).location());
        assertEquals(new Location(0, 1), locationBoundsValidatables.get(1).location());
        assertEquals(new Location(1, 1), locationBoundsValidatables.get(2).location());

        var fieldOwnershipValidatables = validatables.stream()
                .filter(a -> a instanceof FieldOwnershipValidatable)
                .map(FieldOwnershipValidatable.class::cast)
                .toList();
        assertEquals(3, fieldOwnershipValidatables.size());
        assertEquals(context.game().getFields()[0][0], fieldOwnershipValidatables.get(0).field());
        assertEquals(context.game().getFields()[0][1], fieldOwnershipValidatables.get(1).field());
        assertEquals(context.game().getFields()[1][1], fieldOwnershipValidatables.get(2).field());
        assertEquals(context.player(), fieldOwnershipValidatables.get(0).player());
        assertEquals(context.player(), fieldOwnershipValidatables.get(1).player());
        assertEquals(context.player(), fieldOwnershipValidatables.get(2).player());

        var enoughUnitsValidatable = validatables.stream()
                .filter(a -> a instanceof EnoughUnitsValidatable)
                .map(EnoughUnitsValidatable.class::cast)
                .findAny()
                .orElseThrow();
        assertEquals(context.game().getFields()[0][0], enoughUnitsValidatable.field());
        assertEquals(new Army(10, 2, 4), enoughUnitsValidatable.army());
    }
}