package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.LoginData;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralBalanceConfig;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.testConfig.AfterEachDatabaseCleanupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.USER_ID_AUTH_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
    private GeneralBalanceConfig generalConfig;

    @Autowired
    private BasicBoardGeneratorConfig boardGeneratorConfig;

    @Autowired
    private AiPlayerConfig aiPlayerConfig;


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
        validateGame(savedGame, false);
    }

    @Test
    void generateGameAgainstAI() throws Exception {
        var mockAuthentication = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority(USER_ID_AUTH_PREFIX + user1.getId()))).when(mockAuthentication).getAuthorities();

        user2.setId(aiPlayerConfig.getId());
        user2.setNickname(aiPlayerConfig.getName());

        tested.generateGameAgainstAI(mockAuthentication);
        var allSavedGames = gameRepository.findAll();

        //what has been saved
        assertEquals(1, allSavedGames.size());
        var savedGame = allSavedGames.get(0);
        validateGame(savedGame, true);
    }

    void validateGame(Game game, boolean isPlayer2Ready) {
        validatePlayers(game, isPlayer2Ready);
        validateStateManager(game);
        BoardGeneratorServiceTest.validateBoard(
                boardGeneratorConfig,
                game.getPlayers().get(0),
                game.getPlayers().get(1),
                game.getFields()
        );
    }

    void validatePlayers(Game game, boolean isPLayer2Ready) {
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
        assertEquals(300, player1.getRating());

        var player2 = players.get(1);
        assertEquals(user2.getId(), player2.getId());
        assertEquals(user2.getNickname(), player2.getNickname());
        assertEquals(isPLayer2Ready, player2.getIsReady());
        assertEquals(expectedResourceSet, player2.getResources());
        assertEquals(300, player2.getRating());
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