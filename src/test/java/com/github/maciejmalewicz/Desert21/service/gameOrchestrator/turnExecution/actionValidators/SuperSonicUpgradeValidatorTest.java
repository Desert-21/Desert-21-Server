package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SuperSonicUpgradeValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SuperSonicUpgradeValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private Player player;

    private TurnExecutionContext context;

    @Autowired
    private SuperSonicUpgradeValidator tested;

    @BeforeEach
    void setup() {
        player = new Player("AA",
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
    void validateHappyPathNotTargetingRocket() {
        var validatables = List.of(
                new SuperSonicUpgradeValidatable(false)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathEmptyValidatables() {
        var validatables = new ArrayList<SuperSonicUpgradeValidatable>();
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathTargetingRocket() {
        player.setOwnedUpgrades(List.of(
                LabUpgrade.MASS_PRODUCTION,
                LabUpgrade.SUPER_SONIC_ROCKETS
        ));
        var validatables = List.of(
                new SuperSonicUpgradeValidatable(true)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathTargetingRocket() {
        player.setOwnedUpgrades(List.of(
                LabUpgrade.MASS_PRODUCTION,
                LabUpgrade.SCARAB_SCANNERS
        ));
        var validatables = List.of(
                new SuperSonicUpgradeValidatable(true)
        );
        assertFalse(tested.validate(validatables, context));
    }
}