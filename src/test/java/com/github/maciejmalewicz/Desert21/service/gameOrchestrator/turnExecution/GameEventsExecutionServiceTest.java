package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.ArmyTrainingExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.BuildingUpgradeExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.PaymentExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.ResourcesProductionExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ResourcesProductionEvent;
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

    void setupTested() {
        mockUpgradeExecutor = new MockUpgradeExecutor();
        mockUpgradeExecutor = spy(mockUpgradeExecutor);
        mockResourceProductionExecutor = new MockResourcesProductionExecutor();
        mockResourceProductionExecutor = spy(mockResourceProductionExecutor);
        mockArmyTrainingExecutor = new MockArmyTrainingExecutor();
        mockArmyTrainingExecutor = spy(mockArmyTrainingExecutor);
        tested = new GameEventsExecutionService(
                new PaymentExecutor(),
                mockUpgradeExecutor,
                mockResourceProductionExecutor,
                mockArmyTrainingExecutor
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