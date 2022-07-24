package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
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

import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class FirstTurnStartServiceTest {

    private FirstTurnStartService tested;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BasicGameTimer gameTimer;

    private Game game;

    private PlayersNotifier playersNotifier;

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
                        GameState.WAITING_TO_START,
                        DateUtils.millisecondsFromNow(10000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    void setupTested() {
        playersNotifier = mock(PlayersNotifier.class);
        tested = new FirstTurnStartService(
                playersNotifier,
                mock(TimeoutExecutor.class),
                gameRepository,
                gameTimer
        );
    }

    @BeforeEach
    void setup() {
        setupGame();
        setupTested();
    }

    @Test
    void testGetTimeToWaitForTimeout() {
        var timeout = tested.getTimeToWaitForTimeout(game);
        assertEquals(30_000, timeout);
    }

    @Test
    void testStateTransition() {
        var gameCaptor = ArgumentCaptor.forClass(Game.class);
        var notifiableCaptor = ArgumentCaptor.forClass(PlayersNotificationPair.class);

        tested.stateTransition(game);

        var savedGame = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();
        var stateManager = savedGame.getStateManager();
        assertEquals(GameState.AWAITING, stateManager.getGameState());
        assertEquals("AA", stateManager.getCurrentPlayerId());
        assertEquals(1, stateManager.getTurnCounter());
        assertEquals("AA", stateManager.getFirstPlayerId());
        assertNotNull(stateManager.getCurrentStateTimeoutId());
        assertNotNull(stateManager.getTimeout());

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

        var forCurrentPLayer = calledNotification.forCurrentPlayer();
        assertEquals(NEXT_TURN_NOTIFICATION, forCurrentPLayer.type());
        var notificationContent1 = (NextTurnNotification) forCurrentPLayer.content();
        assertNotNull(notificationContent1.timeout());
        assertEquals("AA", notificationContent1.currentPlayerId());

        var forOpponent = calledNotification.forCurrentPlayer();
        assertEquals(NEXT_TURN_NOTIFICATION, forOpponent.type());
        var notificationContent2 = (NextTurnNotification) forOpponent.content();
        assertNotNull(notificationContent2.timeout());
        assertEquals("AA", notificationContent2.currentPlayerId());
    }
}