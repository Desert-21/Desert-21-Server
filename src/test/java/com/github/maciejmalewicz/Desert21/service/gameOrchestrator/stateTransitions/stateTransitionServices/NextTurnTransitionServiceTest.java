package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.GameFinishedNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.GameEndCheckingService;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.GAME_END_NOTIFICATION;
import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class NextTurnTransitionServiceTest {

    private NextTurnTransitionService tested;

    @Autowired
    private GameRepository gameRepository;

    private Game game;

    private PlayersNotifier playersNotifier;
    private GameEndCheckingService gameEndCheckingService;

    void setupTested() {
        playersNotifier = mock(PlayersNotifier.class);
        var gameTimer = mock(BasicGameTimer.class);
        gameEndCheckingService = mock(GameEndCheckingService.class);
        tested = new NextTurnTransitionService(
              playersNotifier,
              mock(TimeoutExecutor.class),
              gameRepository,
                gameTimer, gameEndCheckingService);
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
                new Field[9][9],
                new StateManager(
                        GameState.WAITING_TO_START,
                        DateUtils.millisecondsFromNow(10000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game.getStateManager().setFirstPlayerId("AA");
        game.getStateManager().setTurnCounter(1);
        game = gameRepository.save(game);
    }

    @BeforeEach
    void setup() {
        setupGame();
        setupTested();
    }

    @Test
    void testStateTransitionFromFirstToSecondPLayer() {
        doReturn(Optional.empty()).when(gameEndCheckingService).checkIfGameHasEnded(any());

        tested.stateTransition(game);

        var fromRepo = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();

        assertEquals(GameState.AWAITING, fromRepo.getStateManager().getGameState());
        assertEquals("BB", fromRepo.getStateManager().getCurrentPlayerId());

        var argumentCaptor = ArgumentCaptor.forClass(PlayersNotificationPair.class);
        verify(playersNotifier, times(1)).notifyPlayers(
                eq(fromRepo),
                argumentCaptor.capture()
        );
        var notificationPair = argumentCaptor.getAllValues().stream().findFirst().orElseThrow();
        assertEquals(notificationPair.forCurrentPlayer(), notificationPair.forOpponent());
        var notification = notificationPair.forCurrentPlayer();
        assertEquals(NEXT_TURN_NOTIFICATION, notification.type());
        var notificationContent = (NextTurnNotification) notification.content();
        assertNotNull(notificationContent.timeout());
        assertEquals("BB", notificationContent.currentPlayerId());
        assertNull(fromRepo.getStateManager().getWinnerId());
        assertEquals(1, game.getStateManager().getTurnCounter());
    }

    @Test
    void testStateTransitionFromSecondToFirstPlayer() {
        doReturn(Optional.empty()).when(gameEndCheckingService).checkIfGameHasEnded(any());

        game.getStateManager().setCurrentPlayerId("BB");
        tested.stateTransition(game);

        var fromRepo = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();

        assertEquals(GameState.AWAITING, fromRepo.getStateManager().getGameState());
        assertEquals("AA", fromRepo.getStateManager().getCurrentPlayerId());

        var argumentCaptor = ArgumentCaptor.forClass(PlayersNotificationPair.class);
        verify(playersNotifier, times(1)).notifyPlayers(
                eq(fromRepo),
                argumentCaptor.capture()
        );
        var notificationPair = argumentCaptor.getAllValues().stream().findFirst().orElseThrow();
        assertEquals(notificationPair.forCurrentPlayer(), notificationPair.forOpponent());
        var notification = notificationPair.forCurrentPlayer();
        assertEquals(NEXT_TURN_NOTIFICATION, notification.type());
        var notificationContent = (NextTurnNotification) notification.content();
        assertNotNull(notificationContent.timeout());
        assertEquals("AA", notificationContent.currentPlayerId());
        assertNull(fromRepo.getStateManager().getWinnerId());
        assertEquals(2, fromRepo.getStateManager().getTurnCounter());
    }

    @Test
    void testStateTransitionWhenOneOfPlayersWonTheGame() {
        doReturn(Optional.of("AA")).when(gameEndCheckingService).checkIfGameHasEnded(any());

        tested.stateTransition(game);

        var fromRepo = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();

        assertEquals(GameState.FINISHED, fromRepo.getStateManager().getGameState());

        var argumentCaptor = ArgumentCaptor.forClass(PlayersNotificationPair.class);
        verify(playersNotifier, times(1)).notifyPlayers(
                eq(fromRepo),
                argumentCaptor.capture()
        );
        var notificationPair = argumentCaptor.getAllValues().stream().findFirst().orElseThrow();
        assertEquals(notificationPair.forCurrentPlayer(), notificationPair.forOpponent());
        var notification = notificationPair.forCurrentPlayer();
        assertEquals(GAME_END_NOTIFICATION, notification.type());
        var notificationContent = (GameFinishedNotification) notification.content();
        assertNotNull(notificationContent.winnerId());
        assertEquals("AA", notificationContent.winnerId());
    }
}