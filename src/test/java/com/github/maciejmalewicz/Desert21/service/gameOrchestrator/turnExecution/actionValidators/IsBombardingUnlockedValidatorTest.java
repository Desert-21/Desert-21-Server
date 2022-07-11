package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsBombardingUnlockedValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IsBombardingUnlockedValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;
    private Player player;

    @Autowired
    private IsBombardingUnlockedValidator tested;

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
    void validateNoValidatables() {
        assertTrue(tested.validate(new ArrayList<>(), context));
    }

    @Test
    void validatePlayerOwnsUpgrade() {
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_CANNONS);
        var validatables = List.of(
                new IsBombardingUnlockedValidatable(),
                new IsBombardingUnlockedValidatable()
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validatePlayerDoesNotOwnUpgrade() {
        var validatables = List.of(
                new IsBombardingUnlockedValidatable(),
                new IsBombardingUnlockedValidatable()
        );
        assertFalse(tested.validate(validatables, context));
    }
}