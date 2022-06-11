package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.PathContinuityValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PathContinuityValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private PathContinuityValidator tested;

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
                new PathContinuityValidatable(List.of(
                        new Location(0, 1),
                        new Location(1, 1),
                        new Location(1, 2),
                        new Location(1, 3)
                )),
                new PathContinuityValidatable(List.of(
                        new Location(3, 1),
                        new Location(2, 1),
                        new Location(1, 1),
                        new Location(1, 2)
                ))
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPath() {
        var validatables = List.of(
                new PathContinuityValidatable(List.of(
                        new Location(0, 1),
                        new Location(1, 1),
                        new Location(1, 2),
                        new Location(1, 3)
                )),
                new PathContinuityValidatable(List.of(
                        new Location(3, 1),
                        new Location(2, 1),
                        new Location(2, 0),
                        new Location(1, 2)
                ))
        );
        assertFalse(tested.validate(validatables, context));
    }
}