package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.EnoughUnitsValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnoughUnitsValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private EnoughUnitsValidator tested;

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
        context.game().getFields()[0][0].setArmy(new Army(10, 2, 4));
    }

    @Test
    void validateHappyPath() {
        var validatables = List.of(
                new EnoughUnitsValidatable(new Army(10, 0, 0), context.game().getFields()[0][0]),
                new EnoughUnitsValidatable(new Army(0, 2, 2), context.game().getFields()[0][0]),
                new EnoughUnitsValidatable(new Army(0, 0, 2), context.game().getFields()[0][0])
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPath() {
        var validatables = List.of(
                new EnoughUnitsValidatable(new Army(10, 0, 0), context.game().getFields()[0][0]),
                new EnoughUnitsValidatable(new Army(0, 2, 2), context.game().getFields()[0][0]),
                new EnoughUnitsValidatable(new Army(0, 0, 20), context.game().getFields()[0][0])
        );
        assertFalse(tested.validate(validatables, context));
    }
}