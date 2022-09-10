package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.GamePlayerData;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.SurrenderNotification;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.SURRENDER_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({AfterEachDatabaseCleanupExtension.class})
@SpringBootTest
class SurrenderServiceTest {

    @Autowired
    private GameRepository gameRepository;

    private TimeoutExecutor timeoutExecutor;
    private PlayersNotifier playersNotifier;
    private GamePlayerService gamePlayerService;

    private SurrenderService tested;

    private Game game;
    private Player player;

    private Authentication authentication;

    void setupAuth() {
        var authorities = List.of(
                new SimpleGrantedAuthority("USER_AA"),
                new SimpleGrantedAuthority("Any random authority")
        );
        authentication = mock(Authentication.class);
        doReturn(authorities).when(authentication).getAuthorities();
    }

    void setupGame() {
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        game = new Game(
                List.of(
                        player,
                        new Player("BB",
                                "schabina123456",
                                new ResourceSet(60, 60, 60))),
                BoardUtils.generateEmptyPlain(7),
                new StateManager(
                        GameState.WAITING_TO_START,
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    void setupTested() throws NotAcceptableException, AuthorizationException {

        gamePlayerService = mock(GamePlayerService.class);
        doReturn(new GamePlayerData(game, player)).when(gamePlayerService).getGamePlayerData(anyString(), any());

        timeoutExecutor = mock(TimeoutExecutor.class);
        playersNotifier = mock(PlayersNotifier.class);
        tested = new SurrenderService(
                gamePlayerService,
                gameRepository,
                timeoutExecutor,
                playersNotifier
        );
    }

    @BeforeEach
    void setup() throws NotAcceptableException, AuthorizationException  {
        setupAuth();
        setupGame();
        setupTested();
    }

    @Test
    void surrenderHappyPath() throws NotAcceptableException, AuthorizationException {
        var timeoutIdBefore = game.getStateManager().getCurrentStateTimeoutId();
        var timeoutBefore = game.getStateManager().getTimeout();
        tested.surrender(authentication, "ANY");

        var savedGame = gameRepository.findAll().stream().findAny().orElseThrow();
        assertNotEquals(timeoutIdBefore, savedGame.getStateManager().getCurrentStateTimeoutId());
        assertNotEquals(timeoutBefore, savedGame.getStateManager().getTimeout());
        assertEquals("BB", savedGame.getStateManager().getWinnerId());
        assertEquals(GameState.FINISHED, savedGame.getStateManager().getGameState());

        verify(timeoutExecutor, times(1)).executeTimeoutOnGame(game);
        verify(playersNotifier, times(1)).notifyPlayers(game,
                new Notification<>(SURRENDER_NOTIFICATION, new SurrenderNotification("AA"))
        );
    }
}