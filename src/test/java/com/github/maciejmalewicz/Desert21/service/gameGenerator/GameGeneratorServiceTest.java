package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralConfig;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith({AfterEachDatabaseCleanupExtension.class})
class GameGeneratorServiceTest {

    @Autowired
    private GameGeneratorService tested;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GeneralConfig generalConfig;

    @Autowired
    private BasicBoardGeneratorConfig boardGeneratorConfig;

    private ApplicationUser user1;
    private ApplicationUser user2;

    @BeforeEach
    void prepareUsers() {
        var user1 = new ApplicationUser(
                "macior123456",
                new LoginData("macior@gmail.com", "password")
        );
        var user2 = new ApplicationUser(
                "schabina123456",
                new LoginData("schabina@gmail.com", "password")
        );
        this.user1 = userRepository.save(user1);
        this.user2 = userRepository.save(user2);
    }

    @Test
    void generateGame() {
        tested.generateGame(user1.getId(), user2.getId());
        var allSavedGames = gameRepository.findAll();

        //what has been saved
        assertEquals(1, allSavedGames.size());
        var savedGame = allSavedGames.get(0);
        validateGame(savedGame);
    }

    void validateGame(Game game) {
        validatePlayers(game);
        validateStateManager(game);
        BoardGeneratorServiceTest.validateBoard(
                boardGeneratorConfig,
                game.getPlayers().get(0),
                game.getPlayers().get(1),
                game.getFields()
        );
    }

    void validatePlayers(Game game) {
        var players = game.getPlayers();
        assertEquals(2, players.size());

        var player1 = players.get(0);
        assertEquals(user1.getId(), player1.getId());
        assertEquals(user1.getNickname(), player1.getNickname());
        assertFalse(player1.getIsReady());
        var expectedResourceSet = new ResourceSet(
                generalConfig.getStartingResources(),
                generalConfig.getStartingResources(),
                generalConfig.getStartingResources()
        );
        assertEquals(expectedResourceSet, player1.getResources());

        var player2 = players.get(1);
        assertEquals(user2.getId(), player2.getId());
        assertEquals(user2.getNickname(), player2.getNickname());
        assertFalse(player2.getIsReady());
        assertEquals(expectedResourceSet, player2.getResources());
    }

    void validateStateManager(Game game) {
        var stateManager = game.getStateManager();
        assertEquals(GameState.WAITING_TO_START, stateManager.getGameState());

        assertNotNull(stateManager.getCurrentStateTimeoutId());
        assertNull(stateManager.getCurrentPlayerId());

        var willTimeoutInTheFuture = 0L < stateManager.getTimeout().getTime() - new Date().getTime();
        assertTrue(willTimeoutInTheFuture);
    }


}