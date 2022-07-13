package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AIProductionIncreaseEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_NEXT_TURN;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AIProductionIncreaseExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private Player player;
    private TurnExecutionContext context;

    @Autowired
    private AIProductionIncreaseExecutor tested;

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
    void executePlayerHasProductionAI() throws NotAcceptableException {
        player.getProductionAI().setActivated(true);
        player.getProductionAI().setCurrentProduction(20);
        var results = tested.execute(List.of(new AIProductionIncreaseEvent()), context);

        // returns empty result list
        var eventResults = results.results();
        assertThat(new ArrayList<>(), sameBeanAs(eventResults));

        // increases production AI
        var newContext = results.context();
        var updatedPlayer = newContext.player();
        assertEquals(40, updatedPlayer.getProductionAI().getCurrentProduction());

        // adds a next one to tht event queue
        var updatedGame = newContext.game();
        assertThat(List.of(new AIProductionIncreaseEvent(END_OF_NEXT_TURN)), sameBeanAs(updatedGame.getEventQueue()));
    }

    @Test
    void executePlayerHasNoProductionAI() throws NotAcceptableException {
        player.getProductionAI().setActivated(false);
        player.getProductionAI().setCurrentProduction(0);
        var results = tested.execute(List.of(new AIProductionIncreaseEvent()), context);

        // returns empty result list
        var eventResults = results.results();
        assertThat(new ArrayList<>(), sameBeanAs(eventResults));

        // does not increase production AI
        var newContext = results.context();
        var updatedPlayer = newContext.player();
        assertEquals(0, updatedPlayer.getProductionAI().getCurrentProduction());

        // adds a next one to tht event queue
        var updatedGame = newContext.game();
        assertThat(new ArrayList<>(), sameBeanAs(updatedGame.getEventQueue()));
    }
}