package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
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

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOLUTION_PHASE_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class TurnResolutionPhaseStartServiceTest {

    private PlayersNotifier playersNotifier;

    private TurnResolutionPhaseStartService tested;

    @Autowired
    private GameRepository gameRepository;

    private Game game;

    void setupTested() {
        playersNotifier = mock(PlayersNotifier.class);
        tested = new TurnResolutionPhaseStartService(
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
    void testGetTimeToWaitForTimeout() {
        var timeout = tested.getTimeToWaitForTimeout(game);
        assertEquals(5_000, timeout);
    }

    @Test
    void testStateTransition() {
        var gameCaptor = ArgumentCaptor.forClass(Game.class);
        var notifiableCaptor = ArgumentCaptor.forClass(Notifiable.class);

        tested.stateTransition(game);

        var savedGame = gameRepository.findAll().stream()
                .findAny()
                .orElseThrow();

        var stateManager = savedGame.getStateManager();
        assertEquals(GameState.RESOLVED, stateManager.getGameState());
        assertEquals("AA", stateManager.getCurrentPlayerId());
        assertNotNull(stateManager.getCurrentStateTimeoutId());
        assertNotNull(stateManager.getTimeout());

        verify(playersNotifier, times(1)).notifyPlayers(
                gameCaptor.capture(),
                notifiableCaptor.capture()
        );

        var calledGame = gameCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();
        var calledNotifiable = notifiableCaptor.getAllValues().stream()
                .findAny()
                .orElseThrow();
        assertEquals(game, calledGame);
        assertEquals(0, calledNotifiable.forSpecificPlayer().getSecond().size());
        assertEquals(0, calledNotifiable.forProducer().size());
        assertEquals(0, calledNotifiable.forOpponent().size());
        assertEquals(1, calledNotifiable.forBoth().size());
        assertEquals(RESOLUTION_PHASE_NOTIFICATION, calledNotifiable.forBoth().get(0).type());
    }


}