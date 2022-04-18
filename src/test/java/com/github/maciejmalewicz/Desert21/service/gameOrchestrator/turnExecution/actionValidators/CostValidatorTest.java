package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.CostValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CostValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private CostValidator tested;

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
    void validateCanAffordWhenBelowLimit() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(20, 10, 0)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(0, 0, 30))
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateCanAffordWhenReachingTheLimitMetal() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(20, 10, 0)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(30, 0, 30))
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateCanAffordWhenReachingTheLimitBuildingMaterials() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(20, 10, 0)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(0, 20, 30))
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateCanAffordWhenReachingTheLimitElectricity() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(20, 10, 20)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(0, 0, 30))
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateCanNotAffordWhenExceedingTheLimitMetal() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(21, 10, 0)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(30, 0, 30))
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateCanNotAffordWhenExceedingTheLimitBuildingMaterials() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(20, 11, 0)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(0, 20, 30))
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateCanNotAffordWhenExceedingTheLimitElectricity() {
        var validatables = List.of(
                new CostValidatable(new ResourceSet(20, 10, 21)),
                new CostValidatable(new ResourceSet(10, 30, 10)),
                new CostValidatable(new ResourceSet(0, 0, 30))
        );
        assertFalse(tested.validate(validatables, context));
    }
}