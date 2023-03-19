package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.ai.core.AiTurnHandler;
import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.ResolutionPhaseNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.GameEventsExecutionService;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOLUTION_PHASE_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class TurnResolutionPhaseStartServiceTest {

    private PlayersNotifier playersNotifier;

    private TurnResolutionPhaseStartService tested;

    private ResolutionPhaseNotificationService resolutionPhaseNotificationService;

    @Autowired
    private GameEventsExecutionService gameEventsExecutionService;

    @Autowired
    private GameBalanceService gameBalanceService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AiPlayerConfig aiPlayerConfig;

    @Autowired
    private AiTurnHandler aiTurnHandler;

    private Game game;

    void setupTested() {
        resolutionPhaseNotificationService = mock(ResolutionPhaseNotificationService.class);
        doReturn(new ResolutionPhaseNotificationPair(
                List.of(new Notification<>("N1", null)),
                List.of(new Notification<>("N2", null))
        )).when(resolutionPhaseNotificationService).createNotifications(any(Game.class));
        playersNotifier = mock(PlayersNotifier.class);
        gameEventsExecutionService = spy(gameEventsExecutionService);
        tested = new TurnResolutionPhaseStartService(
                playersNotifier,
                mock(TimeoutExecutor.class),
                gameRepository,
                resolutionPhaseNotificationService,
                gameEventsExecutionService,
                gameBalanceService,
                aiPlayerConfig,
                aiTurnHandler
        );
    }

    void setupGame() {
        game = new Game(
                List.of(
                        new Player("AA",
                                "macior123456",
                                new ResourceSet(60, 60, 60)),
                        new Player("BB",
                                "schabina123456",
                                new ResourceSet(60, 60, 60))),
                BoardUtils.generateEmptyPlain(9),
                new StateManager(
                        GameState.AWAITING,
                        DateUtils.millisecondsFromNow(10000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    @BeforeEach
    void setup() {
        setupGame();
        setupTested();
    }

    @Test
    void testGetTimeToWaitForTimeoutEmptyEventList() {
        var timeout = tested.getTimeToWaitForTimeout(game);
        assertEquals(0, timeout);
    }

    @Test
    void testGetTimeToWaitForTimeoutFilledEventList() {
        game.setCurrentEventResults(List.of(
                () -> 2_000,
                () -> 3_000,
                () -> 1_000
        ));
        var timeout = tested.getTimeToWaitForTimeout(game);
        assertEquals(6_000, timeout);
    }

    @Test
    void testStateTransition() throws NotAcceptableException {
        var gameCaptor = ArgumentCaptor.forClass(Game.class);
        var notifiableCaptor = ArgumentCaptor.forClass(PlayersNotificationPair.class);

        tested.stateTransition(game);

        var savedGame = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();

        var stateManager = savedGame.getStateManager();
        assertEquals(GameState.RESOLVED, stateManager.getGameState());
        assertEquals("AA", stateManager.getCurrentPlayerId());
        assertNotNull(stateManager.getCurrentStateTimeoutId());
        assertNotNull(stateManager.getTimeout());

        verify(gameEventsExecutionService, never()).executeEvents(
                anyList(),
                any(TurnExecutionContext.class)
        );

        verify(playersNotifier, times(1)).notifyPlayers(
                gameCaptor.capture(),
                notifiableCaptor.capture()
        );

        var calledGame = gameCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();
        var calledNotification = notifiableCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();
        assertEquals(game, calledGame);

        var forCurrentPlayer = calledNotification.forCurrentPlayer();
        assertEquals(RESOLUTION_PHASE_NOTIFICATION, forCurrentPlayer.type());
        var resPhaseNotification1 = (ResolutionPhaseNotification) forCurrentPlayer.content();
        assertNotNull(resPhaseNotification1.timeout());
        var actionsNotifications1 = resPhaseNotification1.notifications();
        assertEquals(List.of(new Notification<>("N1", null)), actionsNotifications1);

        var forOpponent = calledNotification.forOpponent();
        assertEquals(RESOLUTION_PHASE_NOTIFICATION, forOpponent.type());
        var resPhaseNotification2 = (ResolutionPhaseNotification) forOpponent.content();
        assertNotNull(resPhaseNotification1.timeout().getTime());
        var actionsNotifications2 = resPhaseNotification2.notifications();
        assertEquals(List.of(new Notification<>("N2", null)), actionsNotifications2);
    }

    @Test
    public void testStateTransitionOnTimeout() throws NotAcceptableException {
        game.getStateManager().setCurrentlyTimedOut(true);

        var gameCaptor = ArgumentCaptor.forClass(Game.class);
        var notifiableCaptor = ArgumentCaptor.forClass(PlayersNotificationPair.class);

        tested.stateTransition(game);

        var savedGame = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();

        var stateManager = savedGame.getStateManager();
        assertEquals(GameState.RESOLVED, stateManager.getGameState());
        assertEquals("AA", stateManager.getCurrentPlayerId());
        assertNotNull(stateManager.getCurrentStateTimeoutId());
        assertNotNull(stateManager.getTimeout());

        verify(gameEventsExecutionService, times(1)).executeEvents(
                anyList(),
                eq(new TurnExecutionContext(
                        gameBalanceService.getGameBalance(),
                        game,
                        game.getCurrentPlayer().get()
                ))
        );

        verify(playersNotifier, times(1)).notifyPlayers(
                gameCaptor.capture(),
                notifiableCaptor.capture()
        );

        var calledGame = gameCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();
        var calledNotification = notifiableCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();
        assertEquals(game, calledGame);

        var forCurrentPlayer = calledNotification.forCurrentPlayer();
        assertEquals(RESOLUTION_PHASE_NOTIFICATION, forCurrentPlayer.type());
        var resPhaseNotification1 = (ResolutionPhaseNotification) forCurrentPlayer.content();
        assertNotNull(resPhaseNotification1.timeout());
        var actionsNotifications1 = resPhaseNotification1.notifications();
        assertEquals(List.of(new Notification<>("N1", null)), actionsNotifications1);

        var forOpponent = calledNotification.forOpponent();
        assertEquals(RESOLUTION_PHASE_NOTIFICATION, forOpponent.type());
        var resPhaseNotification2 = (ResolutionPhaseNotification) forOpponent.content();
        var actionsNotifications2 = resPhaseNotification2.notifications();
        assertEquals(List.of(new Notification<>("N2", null)), actionsNotifications2);

        assertFalse(game.getStateManager().isCurrentlyTimedOut());
    }
}