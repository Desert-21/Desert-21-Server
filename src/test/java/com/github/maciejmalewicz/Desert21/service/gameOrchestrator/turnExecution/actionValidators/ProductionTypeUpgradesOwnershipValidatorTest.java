package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ProductionTypeUpgradesOwnershipValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.TrainingMode;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductionTypeUpgradesOwnershipValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private ProductionTypeUpgradesOwnershipValidator tested;

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
        context.player().getOwnedUpgrades().add(LabUpgrade.MEDIUM_PRODUCTION);
        context.player().getOwnedUpgrades().add(LabUpgrade.MASS_PRODUCTION);

        var validatables = List.of(
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.SMALL_PRODUCTION),
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MEDIUM_PRODUCTION),
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MASS_PRODUCTION)
        );

        var result = tested.validate(validatables, context);
        assertTrue(result);
    }

    @Test
    void validateNoMediumProduction() {
        context.player().getOwnedUpgrades().add(LabUpgrade.MASS_PRODUCTION);

        var validatables = List.of(
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.SMALL_PRODUCTION),
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MEDIUM_PRODUCTION),
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MASS_PRODUCTION)
        );

        var result = tested.validate(validatables, context);
        assertFalse(result);
    }

    @Test
    void validateNoMassProduction() {
        context.player().getOwnedUpgrades().add(LabUpgrade.MEDIUM_PRODUCTION);

        var validatables = List.of(
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.SMALL_PRODUCTION),
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MEDIUM_PRODUCTION),
                new ProductionTypeUpgradesOwnershipValidatable(TrainingMode.MASS_PRODUCTION)
        );

        var result = tested.validate(validatables, context);
        assertFalse(result);
    }
}