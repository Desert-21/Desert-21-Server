package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.GameReadinessService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.FirstTurnStartService;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class GamePlayerServiceTest {

    @Autowired
    private GamePlayerService tested;

    @Autowired
    private GameRepository gameRepository;

    private Game game;

    private Authentication authentication;

    private Player player;

    @BeforeEach
    void setup() {
        setupAuth();
        setupGame();
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
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));

        game = new Game(
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
                )
        );
        game = gameRepository.save(game);
    }

    @Test
    void getHappyPath() throws NotAcceptableException, AuthorizationException {
        var id = game.getId();
        var retrieved = tested.getGamePlayerData(id, authentication);
        assertEquals(player, retrieved.player());
        assertEquals(game, retrieved.game());
    }

    @Test
    void getWithEmptyCredentials() {
        doReturn(new ArrayList<>()).when(authentication).getAuthorities();

        var exception = assertThrows(AuthorizationException.class, () -> {
            var id = game.getId();
            tested.getGamePlayerData(id, authentication);
        });
        assertEquals("User could not be identified!", exception.getMessage());
    }

    @Test
    void getWithGameNotFound() {
        gameRepository.deleteAll();

        var exception = assertThrows(NotAcceptableException.class, () -> {
            var id = game.getId();
            tested.getGamePlayerData(id, authentication);
        });
        assertEquals("Game not found!", exception.getMessage());
    }

    @Test
    void getWithNotParticipatingInGame() {
        doReturn(List.of(
                new SimpleGrantedAuthority("USER_CC")
        )).when(authentication).getAuthorities();

        var exception = assertThrows(NotAcceptableException.class, () -> {
            var id = game.getId();
            tested.getGamePlayerData(id, authentication);
        });
        assertEquals("User is not participating in selected game!", exception.getMessage());
    }
}