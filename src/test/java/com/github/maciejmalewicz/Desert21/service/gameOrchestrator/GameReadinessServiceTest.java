package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.FirstTurnStartService;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class GameReadinessServiceTest {

    private GameReadinessService tested;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerService gamePlayerService;

    private Game game;

    private Authentication authentication;

    private FirstTurnStartService firstTurnStartService;

    void setup() {
        setupAuth();
        setupGame();
        setupTested();
    }

    void setupTested() {
        firstTurnStartService = mock(FirstTurnStartService.class);
        tested = new GameReadinessService(gameRepository, firstTurnStartService, gamePlayerService);
    }

    void setupAuth() {
        var authorities = List.of(
                new SimpleGrantedAuthority("USER_AA"),
                new SimpleGrantedAuthority("Any random authority")
        );
        authentication = mock(Authentication.class);
        doReturn(authorities).when(authentication).getAuthorities();
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
                        DateUtils.millisecondsFromNow(10_000),
                        "AA",
                        "TIMEOUTID"
                )
        );
        game = gameRepository.save(game);
    }

    @Test
    void notifyAboutReadinessFromFirstPLayer() throws NotAcceptableException {
        setup();

        var id = game.getId();
        tested.notifyAboutReadiness(authentication, id);

        verify(firstTurnStartService, never()).stateTransition(any(Game.class));
        var savedGame = gameRepository.findAll().stream().findAny().orElseThrow();
        var savedPlayer = savedGame.getPlayers().stream()
                .filter(p -> p.getId().equals("AA"))
                .findAny()
                .orElseThrow();
        assertTrue(savedPlayer.getIsReady());
    }

    @Test
    void notifyAboutReadinessFromSecondPlayer() throws NotAcceptableException {
        setup();
        var secondPlayer = game.getPlayers().stream()
                .filter(p -> p.getId().equals("BB"))
                .findAny()
                .orElseThrow();
        secondPlayer.setIsReady(true);
        game = gameRepository.save(game);

        var id = game.getId();
        tested.notifyAboutReadiness(authentication, id);

        var firstPlayer = game.getPlayers().stream()
                .filter(p -> p.getId().equals("AA"))
                .findAny()
                .orElseThrow();
        firstPlayer.setIsReady(true);

        verify(firstTurnStartService, times(1)).stateTransition(game);
    }

    @Test
    void notifyAboutReadinessGamePlayerServiceError() {
        setup();
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        assertThrows(NotAcceptableException.class, () -> {
            var id = game.getId();
            tested.notifyAboutReadiness(authentication, id);
        });
    }


}