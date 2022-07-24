package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.LabUpgradeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.LabUpgradeEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LabUpgradeExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;
    private Player player;

    @Autowired
    private LabUpgradeExecutor tested;

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
    void execute() throws NotAcceptableException  {
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        var executables = List.of(
                new LabUpgradeEvent(LabUpgrade.SCARAB_SCANNERS),
                new LabUpgradeEvent(LabUpgrade.REUSABLE_PARTS),
                new LabUpgradeEvent(LabUpgrade.MORE_ELECTRICITY)
        );
        var results = tested.execute(executables, context);
        var newUpgrades = player.getOwnedUpgrades();

        var expectedEventResults = List.of(
                new LabUpgradeEventResult(LabUpgrade.SCARAB_SCANNERS, "AA"),
                new LabUpgradeEventResult(LabUpgrade.REUSABLE_PARTS, "AA"),
                new LabUpgradeEventResult(LabUpgrade.MORE_ELECTRICITY, "AA")
        );
        assertThat(expectedEventResults, sameBeanAs(results.results()));

        var expectedUpgrades = List.of(
                LabUpgrade.HOME_SWEET_HOME,
                LabUpgrade.SCARAB_SCANNERS,
                LabUpgrade.REUSABLE_PARTS,
                LabUpgrade.MORE_ELECTRICITY
        );
        assertThat(expectedUpgrades, sameBeanAs(newUpgrades));
    }

    @Test
    void executeForProductionAI() throws NotAcceptableException  {
        player.getOwnedUpgrades().add(LabUpgrade.HOME_SWEET_HOME);
        player.getOwnedUpgrades().add(LabUpgrade.MORE_METAL);
        player.getOwnedUpgrades().add(LabUpgrade.PRODUCTION_MANAGERS);
        var executables = List.of(
                new LabUpgradeEvent(LabUpgrade.PRODUCTION_AI)
        );
        var results = tested.execute(executables, context);
        var newUpgrades = player.getOwnedUpgrades();

        var expectedEventResults = List.of(
                new LabUpgradeEventResult(LabUpgrade.PRODUCTION_AI, "AA")
        );
        assertThat(expectedEventResults, sameBeanAs(results.results()));

        var expectedUpgrades = List.of(
                LabUpgrade.HOME_SWEET_HOME,
                LabUpgrade.MORE_METAL,
                LabUpgrade.PRODUCTION_MANAGERS,
                LabUpgrade.PRODUCTION_AI
        );
        assertThat(expectedUpgrades, sameBeanAs(newUpgrades));

        var expectedAI = new ProductionAI(true, 0);
        assertThat(expectedAI, sameBeanAs(player.getProductionAI()));
    }
}