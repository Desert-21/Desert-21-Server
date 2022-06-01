package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.NoPendingTrainingsValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.components.UnitType;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NoPendingTrainingsValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private NoPendingTrainingsValidator tested;

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
    }

    @Test
    void validateHappyPath() {
        var validatables = List.of(
                new NoPendingTrainingsValidatable(new Location(0, 0)),
                new NoPendingTrainingsValidatable(new Location(0, 1))
        );
        context.game().setEventQueue(List.of(
                new ArmyTrainingEvent(2, new Location(8, 8), UnitType.CANNON, 10)
        ));
        var result = tested.validate(validatables, context);
        assertTrue(result);
    }

    @Test
    void validateLocationsClashing() {
        var validatables = List.of(
                new NoPendingTrainingsValidatable(new Location(0, 0)),
                new NoPendingTrainingsValidatable(new Location(0, 1))
        );
        context.game().setEventQueue(List.of(
                new ArmyTrainingEvent(2, new Location(0, 0), UnitType.CANNON, 10)
        ));
        var result = tested.validate(validatables, context);
        assertFalse(result);
    }
}