package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.PathLengthValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PathLengthValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private PathLengthValidator tested;

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
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1)
                        ),
                        new Army(20, 0, 10)
                ),
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1)
                        ),
                        new Army(20, 2, 10)
                )
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathDroids() {
        var validatables = List.of(
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1)
                        ),
                        new Army(20, 0, 10)
                ),
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1),
                                new Location(1, 2)
                        ),
                        new Army(20, 0, 0)
                )
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathTanks() {
        var validatables = List.of(
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1)
                        ),
                        new Army(20, 0, 10)
                ),
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1)
                        ),
                        new Army(0, 4, 0)
                )
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathCannons() {
        var validatables = List.of(
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1)
                        ),
                        new Army(20, 0, 10)
                ),
                new PathLengthValidatable(
                        List.of(
                                new Location(0, 0),
                                new Location(0, 1),
                                new Location(1, 1),
                                new Location(1, 2),
                                new Location(2, 2)
                        ),
                        new Army(0, 0, 20)
                )
        );
        assertFalse(tested.validate(validatables, context));
    }
}