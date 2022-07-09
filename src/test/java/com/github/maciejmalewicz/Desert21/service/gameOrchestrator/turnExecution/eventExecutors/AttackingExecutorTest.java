package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.FightingArmy;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.FieldConquestService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EnemyFieldConquestFailed;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EnemyFieldConquestSucceeded;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.UnoccupiedFieldConquestFailed;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.UnoccupiedFieldConquestSucceeded;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AttackingExecutorTest {

    private AttackingExecutor tested;

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    private BattleExecutor battleExecutor;
    private FieldConquestService fieldConquestService;

    @BeforeEach
    void setup() {
        setupContext();
        setupTested();
    }

    void setupContext() {
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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.TOWER), "AA");
    }

    void setupTested() {
        battleExecutor = mock(BattleExecutor.class);
        fieldConquestService = mock(FieldConquestService.class);
        tested = new AttackingExecutor(
                battleExecutor,
                fieldConquestService
        );
    }

    @Test
    void executeShouldThrowExceptionWhenBattleExecutorFails() throws NotAcceptableException {
        var events = List.of(
                new AttackingEvent(new Location(0, 0), new Army(10, 10, 10)),
                new AttackingEvent(new Location(0, 2), new Army(0, 10, 100)),
                new AttackingEvent(new Location(-1, 99), new Army(10, 10, 10))
        );
        doThrow(new NotAcceptableException("Not found!")).when(battleExecutor).executeBattle(any(), any());
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.execute(events, context);
        });
        assertEquals("Attack has failed!", exception.getMessage());
    }

    @Test
    void executeHappyPathAttackerWonAgainstPLayer() throws NotAcceptableException {
        var events = List.of(
                new AttackingEvent(new Location(0, 0), new Army(10, 10, 10)),
                new AttackingEvent(new Location(0, 2), new Army(0, 10, 100)),
                new AttackingEvent(new Location(0, 0), new Army(20, 10, 30))
        );
        doAnswer((a) -> {
            var event = a.getArgument(0, AttackingEvent.class);
            var army = event.getArmy();
            return new BattleResult(
                    new FightingArmy(army.getDroids(), army.getTanks(), army.getCannons(), 0),
                    new FightingArmy(0, 0, 0, 0),
                    new FightingArmy(army.getDroids(), army.getTanks(), army.getCannons(), 0),
                    new FightingArmy(0, 0, 0, 0),
                    true,
                    false
            );
        }).when(battleExecutor)
                .executeBattle(any(AttackingEvent.class), any(TurnExecutionContext.class));
        doAnswer(a ->
            a.getArgument(1, TurnExecutionContext.class)
        ).when(fieldConquestService)
                .executeOptionalFieldConquest(any(Pair.class), any(TurnExecutionContext.class));
        var results = tested.execute(events, context);
        var eventResults = results.results();
        var expectedEventResults = List.of(
                new EnemyFieldConquestSucceeded(
                        new Location(0, 0),
                        new BattleResult(
                                new FightingArmy(30, 20, 40, 0),
                                new FightingArmy(0, 0, 0, 0),
                                new FightingArmy(30, 20, 40, 0),
                                new FightingArmy(0, 0, 0, 0),
                                true,
                                false
                        )
                ),
                new EnemyFieldConquestSucceeded(
                        new Location(0, 2),
                        new BattleResult(
                                new FightingArmy(0, 10, 100, 0),
                                new FightingArmy(0, 0, 0, 0),
                                new FightingArmy(0, 10, 100, 0),
                                new FightingArmy(0, 0, 0, 0),
                                true,
                                false
                        )
                )
        );
        assertThat(expectedEventResults, sameBeanAs(eventResults));
    }

    @Test
    void executeHappyPathAttackerLostAgainstPlayer() throws NotAcceptableException {
        var events = List.of(
                new AttackingEvent(new Location(0, 0), new Army(10, 10, 10)),
                new AttackingEvent(new Location(0, 2), new Army(0, 10, 100)),
                new AttackingEvent(new Location(0, 0), new Army(20, 10, 30))
        );
        doAnswer((a) -> {
            var event = a.getArgument(0, AttackingEvent.class);
            var army = event.getArmy();
            return new BattleResult(
                    new FightingArmy(army.getDroids(), army.getTanks(), army.getCannons(), 0),
                    new FightingArmy(1000, 1000, 1000, 1000),
                    new FightingArmy(0, 0, 0, 0),
                    new FightingArmy(900, 900, 900, 900),
                    false,
                    false
            );
        }).when(battleExecutor)
                .executeBattle(any(AttackingEvent.class), any(TurnExecutionContext.class));
        doAnswer(a ->
                a.getArgument(1, TurnExecutionContext.class)
        ).when(fieldConquestService)
                .executeOptionalFieldConquest(any(Pair.class), any(TurnExecutionContext.class));
        var results = tested.execute(events, context);
        var eventResults = results.results();
        var expectedEventResults = List.of(
                new EnemyFieldConquestFailed(
                        new Location(0, 0),
                        new BattleResult(
                                new FightingArmy(30, 20, 40, 0),
                                new FightingArmy(1000, 1000, 1000, 1000),
                                new FightingArmy(0, 0, 0, 0),
                                new FightingArmy(900, 900, 900, 900),
                                false,
                                false
                        )
                ),
                new EnemyFieldConquestFailed(
                        new Location(0, 2),
                        new BattleResult(
                                new FightingArmy(0, 10, 100, 0),
                                new FightingArmy(1000, 1000, 1000, 1000),
                                new FightingArmy(0, 0, 0, 0),
                                new FightingArmy(900, 900, 900, 900),
                                false,
                                false
                        )
                )
        );
        assertThat(expectedEventResults, sameBeanAs(eventResults));
    }

    @Test
    void executeHappyPathAttackerWonAgainstScarabs() throws NotAcceptableException {
        var events = List.of(
                new AttackingEvent(new Location(0, 0), new Army(10, 10, 10)),
                new AttackingEvent(new Location(0, 2), new Army(0, 10, 100)),
                new AttackingEvent(new Location(0, 0), new Army(20, 10, 30))
        );
        doAnswer((a) -> {
            var event = a.getArgument(0, AttackingEvent.class);
            var army = event.getArmy();
            return new BattleResult(
                    new FightingArmy(army.getDroids(), army.getTanks(), army.getCannons(), 0),
                    new FightingArmy(0, 0, 0, 100),
                    new FightingArmy(army.getDroids(), army.getTanks(), army.getCannons(), 0),
                    new FightingArmy(0, 0, 0, 0),
                    true,
                    true
            );
        }).when(battleExecutor)
                .executeBattle(any(AttackingEvent.class), any(TurnExecutionContext.class));
        doAnswer(a ->
                a.getArgument(1, TurnExecutionContext.class)
        ).when(fieldConquestService)
                .executeOptionalFieldConquest(any(Pair.class), any(TurnExecutionContext.class));
        var results = tested.execute(events, context);
        var eventResults = results.results();
        var expectedEventResults = List.of(
                new UnoccupiedFieldConquestSucceeded(
                        new Location(0, 0),
                        new BattleResult(
                                new FightingArmy(30, 20, 40, 0),
                                new FightingArmy(0, 0, 0, 100),
                                new FightingArmy(30, 20, 40, 0),
                                new FightingArmy(0, 0, 0, 0),
                                true,
                                true
                        )
                ),
                new UnoccupiedFieldConquestSucceeded(
                        new Location(0, 2),
                        new BattleResult(
                                new FightingArmy(0, 10, 100, 0),
                                new FightingArmy(0, 0, 0, 100),
                                new FightingArmy(0, 10, 100, 0),
                                new FightingArmy(0, 0, 0, 0),
                                true,
                                true
                        )
                )
        );
        assertThat(expectedEventResults, sameBeanAs(eventResults));
    }

    @Test
    void executeHappyPathAttackerLostAgainstScarabs() throws NotAcceptableException {
        var events = List.of(
                new AttackingEvent(new Location(0, 0), new Army(10, 10, 10)),
                new AttackingEvent(new Location(0, 2), new Army(0, 10, 100)),
                new AttackingEvent(new Location(0, 0), new Army(20, 10, 30))
        );
        doAnswer((a) -> {
            var event = a.getArgument(0, AttackingEvent.class);
            var army = event.getArmy();
            return new BattleResult(
                    new FightingArmy(army.getDroids(), army.getTanks(), army.getCannons(), 0),
                    new FightingArmy(0, 0, 0, 10000),
                    new FightingArmy(0, 0, 0, 0),
                    new FightingArmy(0, 0, 0, 9000),
                    false,
                    true
            );
        }).when(battleExecutor)
                .executeBattle(any(AttackingEvent.class), any(TurnExecutionContext.class));
        doAnswer(a ->
                a.getArgument(1, TurnExecutionContext.class)
        ).when(fieldConquestService)
                .executeOptionalFieldConquest(any(Pair.class), any(TurnExecutionContext.class));
        var results = tested.execute(events, context);
        var eventResults = results.results();
        var expectedEventResults = List.of(
                new UnoccupiedFieldConquestFailed(
                        new Location(0, 0),
                        new BattleResult(
                                new FightingArmy(30, 20, 40, 0),
                                new FightingArmy(0, 0, 0, 10000),
                                new FightingArmy(0, 0, 0, 0),
                                new FightingArmy(0, 0, 0, 9000),
                                false,
                                true
                        )
                ),
                new UnoccupiedFieldConquestFailed(
                        new Location(0, 2),
                        new BattleResult(
                                new FightingArmy(0, 10, 100, 0),
                                new FightingArmy(0, 0, 0, 10000),
                                new FightingArmy(0, 0, 0, 0),
                                new FightingArmy(0, 0, 0, 9000),
                                false,
                                true
                        )
                )
        );
        assertThat(expectedEventResults, sameBeanAs(eventResults));
    }
}