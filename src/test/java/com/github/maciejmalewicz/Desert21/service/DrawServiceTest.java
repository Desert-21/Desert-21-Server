package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.dto.game.DrawAction;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.GamePlayerData;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class DrawServiceTest {

    private DrawService tested;

    private GamePlayerService gamePlayerService;

    private PlayersNotifier playersNotifier;

    private TimeoutExecutor timeoutExecutor;

    @Autowired
    private GameRepository gameRepository;

    private Game game;
    private Player player;
    private Player opponent;

    void setupGamePlayerService() throws Exception {
        gameRepository.deleteAll();
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        opponent =  new Player("BB",
                "schabina123456",
                new ResourceSet(60, 60, 60));
        game = new Game(
                List.of(
                        player,
                        opponent),
                BoardUtils.generateEmptyPlain(7),
                new StateManager(
                        GameState.AWAITING,
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
        player = game.getCurrentPlayer().orElseThrow();
        gamePlayerService = mock(GamePlayerService.class);
        doReturn(new GamePlayerData(game, player)).when(gamePlayerService).getGamePlayerData(anyString(), any());
    }

    @BeforeEach
    void setupTested() throws Exception {
        setupGamePlayerService();
        playersNotifier = mock(PlayersNotifier.class);
        timeoutExecutor = mock(TimeoutExecutor.class);
        tested = new DrawService(
                gamePlayerService,
                playersNotifier,
                gameRepository,
                timeoutExecutor
        );
    }

    @Test
    void requestDraw() throws Exception {
        tested.draw(game.getId(), null, DrawAction.REQUEST);

        var fromRepo = gameRepository.findAll().stream().findFirst().orElseThrow();

        var player = fromRepo.getCurrentPlayer().orElseThrow();
        assertTrue(player.isOfferingDraw());
        assertNotNull(player.getDrawOfferDisabledTimeout());

        verify(playersNotifier, times(1)).notifyPlayer(
                "BB", new Notification<>(DRAW_REQUESTED_NOTIFICATION, "AA")
        );
    }

    @Test
    void requestDrawTimeForbidden() throws Exception {
        // setup
        var gameBefore = gameRepository.findAll().stream().findFirst().orElseThrow();
        var playerBefore = gameBefore.getCurrentPlayer().orElseThrow();
        playerBefore.setDrawOfferDisabledTimeout(new Date(Long.MAX_VALUE));

        var savedBefore = gameRepository.save(gameBefore);
        doReturn(new GamePlayerData(savedBefore, savedBefore.getCurrentPlayer().orElseThrow()))
                .when(gamePlayerService)
                .getGamePlayerData(anyString(), any());

        assertThrows(NotAcceptableException.class, () -> {
            tested.draw(game.getId(), null, DrawAction.REQUEST);
        });

        var fromRepo = gameRepository.findAll().stream().findFirst().orElseThrow();

        var player = fromRepo.getCurrentPlayer().orElseThrow();
        assertFalse(player.isOfferingDraw());

        verify(playersNotifier, never()).notifyPlayer(
                anyString(), any()
        );
    }

    @Test
    void cancelDraw() throws Exception {
        // setup
        var gameBefore = gameRepository.findAll().stream().findFirst().orElseThrow();
        var playerBefore = gameBefore.getCurrentPlayer().orElseThrow();
        playerBefore.setOfferingDraw(true);
        playerBefore.setDrawOfferDisabledTimeout(new Date(Long.MAX_VALUE));

        var savedBefore = gameRepository.save(gameBefore);
        doReturn(new GamePlayerData(savedBefore, savedBefore.getCurrentPlayer().orElseThrow()))
                .when(gamePlayerService)
                .getGamePlayerData(anyString(), any());

        tested.draw(game.getId(), null, DrawAction.CANCEL);

        var fromRepo = gameRepository.findAll().stream().findFirst().orElseThrow();
        var player = fromRepo.getCurrentPlayer().orElseThrow();
        assertFalse(player.isOfferingDraw());
    }

    @Test
    void acceptDraw() throws Exception {
        // setup
        var gameBefore = gameRepository.findAll().stream().findFirst().orElseThrow();
        var playerBefore = gameBefore.getCurrentPlayer().orElseThrow();
        playerBefore.setOfferingDraw(true);
        playerBefore.setDrawOfferDisabledTimeout(new Date(Long.MAX_VALUE));

        var savedBefore = gameRepository.save(gameBefore);
        doReturn(new GamePlayerData(savedBefore, savedBefore.getOtherPlayer().orElseThrow()))
                .when(gamePlayerService)
                .getGamePlayerData(anyString(), any());

        tested.draw(game.getId(), null, DrawAction.ACCEPT);

        var fromRepo = gameRepository.findAll().stream().findFirst().orElseThrow();
        var stateManager = fromRepo.getStateManager();
        assertNotNull(stateManager.getTimeout());
        assertNotNull(stateManager.getCurrentStateTimeoutId());
        assertNull(stateManager.getWinnerId());
        assertEquals(GameState.FINISHED, stateManager.getGameState());

        verify(timeoutExecutor, times(1)).executeTimeoutOnGame(fromRepo);
        verify(playersNotifier, times(1)).notifyPlayers(
                fromRepo, new Notification<>(DRAW_ACCEPTED_NOTIFICATION, null)
        );
    }

    @Test
    void acceptDrawFailWhenCancelled() throws Exception {
        // setup default - player is NOT offering draw
        assertThrows(NotAcceptableException.class, () -> {
            tested.draw(game.getId(), null, DrawAction.ACCEPT);
        });

        var fromRepo = gameRepository.findAll().stream().findFirst().orElseThrow();
        var stateManager = fromRepo.getStateManager();
        assertEquals(GameState.AWAITING, stateManager.getGameState());

        verify(timeoutExecutor, never()).executeTimeoutOnGame(any());
        verify(playersNotifier, never()).notifyPlayers(
                any(Game.class), any(Notification.class)
        );
    }

    @Test
    void rejectDraw() throws Exception {
        // setup
        var gameBefore = gameRepository.findAll().stream().findFirst().orElseThrow();
        var playerBefore = gameBefore.getCurrentPlayer().orElseThrow();
        playerBefore.setOfferingDraw(true);
        playerBefore.setDrawOfferDisabledTimeout(new Date(Long.MAX_VALUE));

        var savedBefore = gameRepository.save(gameBefore);
        doReturn(new GamePlayerData(savedBefore, savedBefore.getOtherPlayer().orElseThrow()))
                .when(gamePlayerService)
                .getGamePlayerData(anyString(), any());

        tested.draw(game.getId(), null, DrawAction.REJECT);

        var fromRepo = gameRepository.findAll().stream().findFirst().orElseThrow();
        var opponent = fromRepo.getOtherPlayer().orElseThrow();

        assertFalse(opponent.isOfferingDraw());

        verify(playersNotifier, times(1)).notifyPlayer(
                "AA", new Notification<>(DRAW_REJECTED_NOTIFICATION, "BB")
        );
    }

    @Test
    void rejectDrawFailWhenCancelled() throws Exception {
        // setup default - player is NOT offering draw
        assertThrows(NotAcceptableException.class, () -> {
            tested.draw(game.getId(), null, DrawAction.REJECT);
        });
        verify(playersNotifier, never()).notifyPlayers(
                any(Game.class), any(Notification.class)
        );
    }

}