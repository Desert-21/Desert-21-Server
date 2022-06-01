package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.TrainingMode;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.UnitType;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrainActionTest {

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
    void getActionValidatablesLocationOutOfBounds() {
        var trainAction = new TrainAction(new Location(20, 20), UnitType.TANK, TrainingMode.MASS_PRODUCTION);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            trainAction.getActionValidatables(context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }

    @Test
    void getActionValidatablesEmptyUnitType() {
        var trainAction = new TrainAction(new Location(7, 7), null, TrainingMode.MASS_PRODUCTION);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            trainAction.getActionValidatables(context);
        });
        assertEquals("Invalid units training data!", exception.getMessage());
    }

    @Test
    void getActionValidatablesEmptyTrainingMode() {
        var trainAction = new TrainAction(new Location(7, 7), UnitType.TANK, null);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            trainAction.getActionValidatables(context);
        });
        assertEquals("Invalid units training data!", exception.getMessage());
    }

    @Test
    void getActionValidatablesHappyPath() throws NotAcceptableException {
        var trainAction = new TrainAction(new Location(2, 2), UnitType.TANK, TrainingMode.MASS_PRODUCTION);
        var validatables = trainAction.getActionValidatables(context);
        assertEquals(6, validatables.size());

        assertEquals(
                new FieldOwnershipValidatable(
                        BoardUtils.fieldAtLocation(context.game().getFields(), new Location(2, 2)),
                        context.player()
                ),
                validatables.get(0)
        );
        assertEquals(new CostValidatable(new ResourceSet(500, 0, 0)), validatables.get(1));
        assertEquals(new SingleTrainingPerLocationValidatable(new Location(2, 2)), validatables.get(2));
        assertEquals(new NoPendingTrainingsValidatable(new Location(2, 2)), validatables.get(3));
        assertEquals(new BuildingSufficientForUnitsTrainingValidatable(
                BoardUtils.fieldAtLocation(context.game().getFields(), new Location(2, 2)).getBuilding(),
                UnitType.TANK
        ), validatables.get(4));
        assertEquals(new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MASS_PRODUCTION), validatables.get(5));
    }

    @Test
    void getEventExecutablesHappyPathDroids() throws NotAcceptableException {
        var trainAction = new TrainAction(new Location(0, 0), UnitType.DROID, TrainingMode.SMALL_PRODUCTION);
        var executables = trainAction.getEventExecutables(context);
        assertEquals(2, executables.size());

        var paymentEvent = (PaymentEvent) executables.get(0);
        assertEquals(new ResourceSet(50, 0, 0), paymentEvent.getResourceSet());

        var trainingEvent = (ArmyTrainingEvent) executables.get(1);
        assertEquals(0, trainingEvent.getTurnsToExecute());
        assertEquals(new Location(0, 0), trainingEvent.getLocation());
        assertEquals(UnitType.DROID, trainingEvent.getUnitType());
        assertEquals(10, trainingEvent.getAmount());
    }

    @Test
    void getEventExecutablesHappyPathTanks() throws NotAcceptableException {
        var trainAction = new TrainAction(new Location(0, 0), UnitType.TANK, TrainingMode.MEDIUM_PRODUCTION);
        var executables = trainAction.getEventExecutables(context);
        assertEquals(2, executables.size());

        var paymentEvent = (PaymentEvent) executables.get(0);
        assertEquals(new ResourceSet(220, 0, 0), paymentEvent.getResourceSet());

        var trainingEvent = (ArmyTrainingEvent) executables.get(1);
        assertEquals(4, trainingEvent.getTurnsToExecute());
        assertEquals(new Location(0, 0), trainingEvent.getLocation());
        assertEquals(UnitType.TANK, trainingEvent.getUnitType());
        assertEquals(11, trainingEvent.getAmount());
    }

    @Test
    void getEventExecutablesHappyPathCannons() throws NotAcceptableException {
        var trainAction = new TrainAction(new Location(0, 0), UnitType.CANNON, TrainingMode.MASS_PRODUCTION);
        var executables = trainAction.getEventExecutables(context);
        assertEquals(2, executables.size());

        var paymentEvent = (PaymentEvent) executables.get(0);
        assertEquals(new ResourceSet(500, 0, 0), paymentEvent.getResourceSet());

        var trainingEvent = (ArmyTrainingEvent) executables.get(1);
        assertEquals(2, trainingEvent.getTurnsToExecute());
        assertEquals(new Location(0, 0), trainingEvent.getLocation());
        assertEquals(UnitType.CANNON, trainingEvent.getUnitType());
        assertEquals(50, trainingEvent.getAmount());
    }
}
