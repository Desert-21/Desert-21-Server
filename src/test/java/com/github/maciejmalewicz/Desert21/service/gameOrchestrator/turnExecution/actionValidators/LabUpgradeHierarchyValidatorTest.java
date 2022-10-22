package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LabUpgradeHierarchyValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LabUpgradeHierarchyValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    private Player player;

    @Autowired
    private LabUpgradeHierarchyValidator tested;

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
    void validateHappyPathBaseUpgrades() {
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.REUSABLE_PARTS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.SCARAB_SCANNERS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.HOME_SWEET_HOME)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathFirstTier() {
        player.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        player.getOwnedUpgrades().add(LabUpgrade.SCARAB_SCANNERS);
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.IMPROVED_CANNONS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.KING_OF_DESERT),
                new LabUpgradeHierarchyValidatable(LabUpgrade.MORE_METAL)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathFirstTier() {
        player.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        player.getOwnedUpgrades().add(LabUpgrade.SCARAB_SCANNERS);
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.IMPROVED_TANKS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.KING_OF_DESERT),
                new LabUpgradeHierarchyValidatable(LabUpgrade.MORE_METAL)
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathSecondTier() {
        player.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        player.getOwnedUpgrades().add(LabUpgrade.SCARAB_SCANNERS);
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);
        player.getOwnedUpgrades().add(LabUpgrade.KING_OF_DESERT);
        player.getOwnedUpgrades().add(LabUpgrade.MORE_METAL);
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.IMPROVED_CANNONS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.THE_GREAT_FORTRESS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.PRODUCTION_MANAGERS)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathSecondTier() {
        player.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        player.getOwnedUpgrades().add(LabUpgrade.SCARAB_SCANNERS);
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);
        player.getOwnedUpgrades().add(LabUpgrade.MORE_METAL);
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.IMPROVED_CANNONS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.GOLD_DIGGERS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.PRODUCTION_MANAGERS)
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathSuperUpgrades() {
        player.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        player.getOwnedUpgrades().add(LabUpgrade.SCARAB_SCANNERS);
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);
        player.getOwnedUpgrades().add(LabUpgrade.KING_OF_DESERT);
        player.getOwnedUpgrades().add(LabUpgrade.MORE_METAL);
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_CANNONS);
        player.getOwnedUpgrades().add(LabUpgrade.FACTORY_TURRET);
        player.getOwnedUpgrades().add(LabUpgrade.PRODUCTION_MANAGERS);
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.ADVANCED_TACTICS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.SUPER_SONIC_ROCKETS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.PRODUCTION_AI)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathSuperUpgrades() {
        player.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        player.getOwnedUpgrades().add(LabUpgrade.SCARAB_SCANNERS);
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);
        player.getOwnedUpgrades().add(LabUpgrade.KING_OF_DESERT);
        player.getOwnedUpgrades().add(LabUpgrade.MORE_METAL);
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_CANNONS);
        player.getOwnedUpgrades().add(LabUpgrade.PRODUCTION_MANAGERS);
        var validatables = List.of(
                new LabUpgradeHierarchyValidatable(LabUpgrade.ADVANCED_TACTICS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.SUPER_SONIC_ROCKETS),
                new LabUpgradeHierarchyValidatable(LabUpgrade.PRODUCTION_AI)
        );
        assertFalse(tested.validate(validatables, context));
    }


}