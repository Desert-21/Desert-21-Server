package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BombardingBattleExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.FieldConquestService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.*;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_NEXT_TURN;
import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_THIS_TURN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameEventsExecutionServiceTest {

    private GameEventsExecutionService tested;

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    private List<Action> actions;

    private BuildingUpgradeEvent actionEvent;
    private BuildingUpgradeEvent queueEvent;

    private BuildingUpgradeEvent futureActionEvent;
    private BuildingUpgradeEvent futureQueueEvent;

    private MockUpgradeExecutor mockUpgradeExecutor;
    private MockResourcesProductionExecutor mockResourceProductionExecutor;
    private MockArmyTrainingExecutor mockArmyTrainingExecutor;
    private MockArmyLeavingExecutor mockArmyLeavingExecutor;
    private MockArmyEnteringExecutor mockArmyEnteringExecutor;
    private MockAttackingExecutor mockAttackingExecutor;
    private MockLabUpgradeExecutor mockLabUpgradeExecutor;
    private MockRocketStrikeExecutor mockRocketStrikeExecutor;
    private MockBuildBuildingExecutor mockBuildBuildingExecutor;
    private MockBombardingExecutor mockBombardingExecutor;

    static class MockAttackingExecutor extends AttackingExecutor {
        public MockAttackingExecutor() {
            super(mock(BattleExecutor.class), mock(FieldConquestService.class));
        }

        @Override
        public EventExecutionResult execute(List<AttackingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<AttackingEvent> getExecutableClass() {
            return AttackingEvent.class;
        }
    }

    static class MockArmyEnteringExecutor extends ArmyEnteringExecutor {
        @Override
        public EventExecutionResult execute(List<ArmyEnteringEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<ArmyEnteringEvent> getExecutableClass() {
            return ArmyEnteringEvent.class;
        }
    }

    static class MockArmyLeavingExecutor extends ArmyLeavingExecutor {
        @Override
        public EventExecutionResult execute(List<ArmyLeavingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<ArmyLeavingEvent> getExecutableClass() {
            return ArmyLeavingEvent.class;
        }
    }

    static class MockArmyTrainingExecutor extends ArmyTrainingExecutor {
        @Override
        public EventExecutionResult execute(List<ArmyTrainingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<ArmyTrainingEvent> getExecutableClass() {
            return ArmyTrainingEvent.class;
        }
    }

    static class MockUpgradeExecutor extends BuildingUpgradeExecutor {
        @Override
        public EventExecutionResult execute(List<BuildingUpgradeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<BuildingUpgradeEvent> getExecutableClass() {
            return BuildingUpgradeEvent.class;
        }
    }

    static class MockResourcesProductionExecutor extends ResourcesProductionExecutor {
        @Override
        public Class<ResourcesProductionEvent> getExecutableClass() {
            return ResourcesProductionEvent.class;
        }

        @Override
        public EventExecutionResult execute(List<ResourcesProductionEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }
    }

    static class MockLabUpgradeExecutor extends LabUpgradeExecutor {
        @Override
        public EventExecutionResult execute(List<LabUpgradeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<LabUpgradeEvent> getExecutableClass() {
            return LabUpgradeEvent.class;
        }
    }

    static class MockRocketStrikeExecutor extends RocketStrikeExecutor {
        @Override
        public EventExecutionResult execute(List<RocketStrikeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<RocketStrikeEvent> getExecutableClass() {
            return RocketStrikeEvent.class;
        }
    }

    static class MockBuildBuildingExecutor extends BuildBuildingExecutor {
        @Override
        public EventExecutionResult execute(List<BuildBuildingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<BuildBuildingEvent> getExecutableClass() {
            return BuildBuildingEvent.class;
        }
    }

    static class MockBombardingExecutor extends BombardingExecutor {
        public MockBombardingExecutor() {
            super(mock(BombardingBattleExecutor.class));
        }

        @Override
        public EventExecutionResult execute(List<BombardingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        @Override
        public Class<BombardingEvent> getExecutableClass() {
            return BombardingEvent.class;
        }
    }

    void setupTested() {
        mockUpgradeExecutor = spy(new MockUpgradeExecutor());
        mockResourceProductionExecutor = spy(new MockResourcesProductionExecutor());
        mockArmyTrainingExecutor = spy(new MockArmyTrainingExecutor());
        mockArmyLeavingExecutor = spy(new MockArmyLeavingExecutor());
        mockArmyEnteringExecutor = spy(new MockArmyEnteringExecutor());
        mockAttackingExecutor = spy(new MockAttackingExecutor());
        mockLabUpgradeExecutor = spy(new MockLabUpgradeExecutor());
        mockRocketStrikeExecutor = spy(new MockRocketStrikeExecutor());
        mockBuildBuildingExecutor = spy(new MockBuildBuildingExecutor());
        mockBombardingExecutor = spy(new MockBombardingExecutor());
        tested = new GameEventsExecutionService(
                new PaymentExecutor(),
                mockUpgradeExecutor,
                mockResourceProductionExecutor,
                mockArmyTrainingExecutor,
                mockArmyLeavingExecutor,
                mockArmyEnteringExecutor,
                mockAttackingExecutor,
                mockLabUpgradeExecutor,
                mockRocketStrikeExecutor,
                mockBuildBuildingExecutor,
                mockBombardingExecutor
        );
    }

    void setupEvents() {
        actionEvent = new BuildingUpgradeEvent();
        actionEvent.setTurnsToExecute(END_OF_THIS_TURN);

        queueEvent = new BuildingUpgradeEvent();
        queueEvent.setTurnsToExecute(END_OF_THIS_TURN);

        futureActionEvent = new BuildingUpgradeEvent();
        futureActionEvent.setTurnsToExecute(END_OF_NEXT_TURN);

        futureQueueEvent = new BuildingUpgradeEvent();
        futureQueueEvent.setTurnsToExecute(END_OF_NEXT_TURN);
    }

    void setupActions() {
        actions = List.of(
                new Action() {
                    @Override
                    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
                        return List.of(actionEvent, futureActionEvent);
                    }
                }
        );
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
                                GameState.WAITING_TO_START,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        ),
                        List.of(queueEvent, futureQueueEvent)
                ),
                player
        );
    }

    @BeforeEach
    void setup() {
        setupTested();
        setupEvents();
        setupActions();
        setupContext();
    }

    @Test
    void executeEventsHappyPath() throws NotAcceptableException {
        tested.executeEvents(actions, context);
        verify(mockUpgradeExecutor, times(1)).execute(List.of(actionEvent, queueEvent), context);
        verify(mockResourceProductionExecutor, times(1)).execute(anyList(), eq(context));
        verify(mockArmyTrainingExecutor, times(1)).execute(anyList(), eq(context));
        verify(mockArmyLeavingExecutor, times(1)).execute(anyList(), eq(context));
        verify(mockArmyEnteringExecutor, times(1)).execute(anyList(), eq(context));
        verify(mockAttackingExecutor, times(1)).execute(anyList(), eq(context));
        verify(mockLabUpgradeExecutor, times(1)).execute(anyList(), eq(context));
        verify(mockRocketStrikeExecutor, times(1)).execute(any(), eq(context));
        verify(mockBuildBuildingExecutor, times(1)).execute(any(), eq(context));
        verify(mockBombardingExecutor, times(1)).execute(any(), eq(context));

        var queue = context.game().getEventQueue();
        assertEquals(2, queue.size());
        assertEquals(futureActionEvent, queue.get(0));
        assertEquals(futureQueueEvent, queue.get(1));
        assertEquals(1, futureActionEvent.getTurnsToExecute());
        assertEquals(1, futureQueueEvent.getTurnsToExecute());
    }

    @Test
    void executeEventsFailingAction() throws NotAcceptableException {
        var failingAction = mock(Action.class);
        doThrow(new NotAcceptableException("TEST EXCEPTION")).when(failingAction).getEventExecutables(any());
        actions = List.of(failingAction);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeEvents(actions, context);
        });
        assertEquals("TEST EXCEPTION", exception.getMessage());
    }
}