package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.CostValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LabUpgradeHierarchyValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LabUpgradeNotRepeatedValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleUpgradePerBranchValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.LabUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
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
class LabActionTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

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
    void getActionValidatablesHappyPath() throws NotAcceptableException {
        var action = new LabAction(LabUpgrade.KING_OF_DESERT);
        var validatables = action.getActionValidatables(context);
        var expectedValidatables = List.of(
                new CostValidatable(new ResourceSet(0, 0, 200)),
                new SingleUpgradePerBranchValidatable(LabUpgrade.KING_OF_DESERT),
                new LabUpgradeHierarchyValidatable(LabUpgrade.KING_OF_DESERT),
                new LabUpgradeNotRepeatedValidatable(LabUpgrade.KING_OF_DESERT)
        );
        assertThat(expectedValidatables, sameBeanAs(validatables));
    }

    @Test
    void getEventsExecutablesHappyPath() throws NotAcceptableException {
        var action = new LabAction(LabUpgrade.KING_OF_DESERT);
        var events = action.getEventExecutables(context);
        var expectedEvents = List.of(
                new LabUpgradeEvent(LabUpgrade.KING_OF_DESERT),
                new PaymentEvent(new ResourceSet(0, 0, 200))
        );
        assertThat(expectedEvents, sameBeanAs(events));
    }
}