package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BattleExecutorTest {

    private AgainstPlayerBattleExecutor mockAgainstPlayerBattleExecutor;
    private AgainstScarabsBattleExecutor mockAgainstScarabsBattleExecutor;

    private BattleResult againstScarabsBattleResult;
    private BattleResult againstPlayerBattleResult;

    private BattleExecutor tested;

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @BeforeEach
    void setup() throws NotAcceptableException {
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

        setupTested();
    }

    void setupTested() throws NotAcceptableException {
        againstScarabsBattleResult = new BattleResult(
                new FightingArmy(10, 2, 4, 0),
                new FightingArmy(0, 0, 0, 20),
                new FightingArmy(5, 1, 2, 0),
                new FightingArmy(0, 0, 0, 0),
                true,
                true
        );
        mockAgainstScarabsBattleExecutor = mock(AgainstScarabsBattleExecutor.class);
        doReturn(againstScarabsBattleResult).when(mockAgainstScarabsBattleExecutor).executeBattleAgainstScarabs(any(Army.class), any(TurnExecutionContext.class));

        againstPlayerBattleResult = new BattleResult(
                new FightingArmy(10, 2, 4, 0),
                new FightingArmy(10, 0, 0, 20),
                new FightingArmy(3, 1, 1, 0),
                new FightingArmy(0, 0, 0, 0),
                true,
                false
        );
        mockAgainstPlayerBattleExecutor = mock(AgainstPlayerBattleExecutor.class);
        doReturn(againstPlayerBattleResult)
                .when(mockAgainstPlayerBattleExecutor)
                .executeBattleAgainstPlayer(any(Army.class), any(TurnExecutionContext.class), any(Field.class));
        tested = new BattleExecutor(
                mockAgainstScarabsBattleExecutor,
                mockAgainstPlayerBattleExecutor
        );
    }

    @Test
    void executeBattleAgainstScarabs() throws NotAcceptableException {
        context.game().getFields()[0][0].setOwnerId(null);
        var attack = new AttackingEvent(new Location(0, 0), new Army(10, 2, 4));
        var battleResult = tested.executeBattle(attack, context);
        assertThat(againstScarabsBattleResult, sameBeanAs(battleResult));
        verify(mockAgainstScarabsBattleExecutor, times(1)).executeBattleAgainstScarabs(
                new Army(10, 2, 4),
                context
        );
    }

    @Test
    void executeBattleAgainstPlayer() throws NotAcceptableException {
        context.game().getFields()[0][0].setOwnerId("BB");
        var attack = new AttackingEvent(new Location(0, 0), new Army(10, 2, 4));
        var battleResult = tested.executeBattle(attack, context);
        assertThat(againstPlayerBattleResult, sameBeanAs(battleResult));
        verify(mockAgainstPlayerBattleExecutor, times(1)).executeBattleAgainstPlayer(
                new Army(10, 2, 4),
                context,
                context.game().getFields()[0][0]
        );
    }

    @Test
    void executeBattleLocationOutOfBounds() {
        var attack = new AttackingEvent(new Location(-1, 99), new Army(10, 2, 4));
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeBattle(attack, context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }
}