package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleUpgradePerBranchValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SingleUpgradePerBranchValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private SingleUpgradePerBranchValidator tested;

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
        var distinctUpgradeValidatables = List.of(
                new SingleUpgradePerBranchValidatable(LabUpgrade.IMPROVED_CANNONS),
                new SingleUpgradePerBranchValidatable(LabUpgrade.SCARAB_SCANNERS),
                new SingleUpgradePerBranchValidatable(LabUpgrade.MORE_METAL)
        );
        assertTrue(tested.validate(distinctUpgradeValidatables, context));
    }

    @Test
    void validateUnhappyPath() {
        var distinctUpgradeValidatables = List.of(
                new SingleUpgradePerBranchValidatable(LabUpgrade.IMPROVED_CANNONS),
                new SingleUpgradePerBranchValidatable(LabUpgrade.SCARAB_SCANNERS),
                new SingleUpgradePerBranchValidatable(LabUpgrade.MORE_METAL),
                new SingleUpgradePerBranchValidatable(LabUpgrade.MORE_BUILDING_MATERIALS)
        );
        assertFalse(tested.validate(distinctUpgradeValidatables, context));
    }
}