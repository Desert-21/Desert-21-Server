package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.HasUpgradeRequiredToBuildValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HasUpgradeRequiredToBuildValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;
    private Player player;

    @Autowired
    private HasUpgradeRequiredToBuildValidator tested;

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
    void validateNoUpgradesRequired() {
        var validatables = List.of(
                new HasUpgradeRequiredToBuildValidatable(BuildingType.HOME_BASE),
                new HasUpgradeRequiredToBuildValidatable(BuildingType.ROCKET_LAUNCHER),
                new HasUpgradeRequiredToBuildValidatable(BuildingType.HOME_BASE)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateOwnedUpgradesRequired() {
        player.getOwnedUpgrades().add(LabUpgrade.FACTORY_BUILDERS);
        player.getOwnedUpgrades().add(LabUpgrade.TOWER_CREATOR);

        assertTrue(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.METAL_FACTORY)), context));
        assertTrue(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.BUILDING_MATERIALS_FACTORY)), context));
        assertTrue(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.ELECTRICITY_FACTORY)), context));
        assertTrue(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.TOWER)), context));
    }

    @Test
    void validateNotOwnedUpgradesRequired() {
        assertFalse(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.METAL_FACTORY)), context));
        assertFalse(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.BUILDING_MATERIALS_FACTORY)), context));
        assertFalse(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.ELECTRICITY_FACTORY)), context));
        assertFalse(tested.validate(List.of(new HasUpgradeRequiredToBuildValidatable(BuildingType.TOWER)), context));
    }
}