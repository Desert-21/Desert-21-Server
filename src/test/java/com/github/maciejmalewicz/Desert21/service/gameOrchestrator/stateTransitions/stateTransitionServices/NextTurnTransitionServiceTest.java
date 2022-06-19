package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    void setupTested() {
        playersNotifier = mock(PlayersNotifier.class);
        tested = new NextTurnTransitionService(
              playersNotifier,
              mock(TimeoutExecutor.class),
              gameRepository
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
        assertEquals(1, game.getStateManager().getTurnCounter());
    }

    @Test
    void testStateTransitionFromSecondToFirstPlayer() {
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
        assertEquals(2, fromRepo.getStateManager().getTurnCounter());
    }
}