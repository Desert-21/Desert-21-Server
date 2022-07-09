package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleBuildPerLocationValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SingleBuildPerLocationValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private SingleBuildPerLocationValidator tested;

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
    void validateHappyPath() {
        var validatables = List.of(
                new SingleBuildPerLocationValidatable(new Location(0, 0)),
                new SingleBuildPerLocationValidatable(new Location(1, 2)),
                new SingleBuildPerLocationValidatable(new Location(2, 2))
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPath() {
        var validatables = List.of(
                new SingleBuildPerLocationValidatable(new Location(0, 0)),
                new SingleBuildPerLocationValidatable(new Location(1, 2)),
                new SingleBuildPerLocationValidatable(new Location(2, 2)),
                new SingleBuildPerLocationValidatable(new Location(1, 2))
        );
        assertFalse(tested.validate(validatables, context));
    }
}