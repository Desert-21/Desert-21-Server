package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.BombardAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BombardingBattleExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.FightingArmy;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BombardingSucceededEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BombardingEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
class BombardingExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private BombardingExecutor tested;

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.METAL_FACTORY), "BB");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.EMPTY_FIELD), "BB");
    }

    @Test
    void executeHappyPathMixed() throws NotAcceptableException {
        var events = List.of(
                new BombardingEvent(new Location(0, 0), 10),
                new BombardingEvent(new Location(1, 1), 20),
                new BombardingEvent(new Location(0, 0), 10),
                new BombardingEvent(new Location(0, 0), 10)
        );
        context.game().getFields()[0][0].setArmy(new Army(1, 1, 1));
        context.game().getFields()[1][1].setArmy(new Army(10, 10, 10));
        var results = tested.execute(events, context);
        var eventResults = results.results();
        var expectedEventResults = List.of(
                new BombardingSucceededEventResult(
                        new Location(1, 1),
                        new BattleResult(
                                new FightingArmy(0, 0, 20, 0),
                                new FightingArmy(10, 10, 10, 0),
                                new FightingArmy(0, 0, 20, 0),
                                new FightingArmy(9, 9, 9, 0),
                                false,
                                false
                        )
                ),
                new BombardingSucceededEventResult(
                        new Location(0, 0),
                        new BattleResult(
                                new FightingArmy(0, 0, 30, 0),
                                new FightingArmy(1, 1, 1, 0),
                                new FightingArmy(0, 0, 30, 0),
                                new FightingArmy(0, 0, 0, 0),
                                true,
                                false
                        )
                )
        );
        assertThat(expectedEventResults, sameBeanAs(eventResults));

        var newContext = results.context();

        var field1 = newContext.game().getFields()[0][0];
        var expectedArmy1 = new Army(0, 0, 0);
        assertThat(expectedArmy1, sameBeanAs(field1.getArmy()));

        var field2 = newContext.game().getFields()[1][1];
        var expectedArmy2 = new Army(9, 9, 9);
        assertThat(expectedArmy2, sameBeanAs(field2.getArmy()));
    }
}