package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralBalanceConfig;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.GameStartService;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameGeneratorService {

    private final BoardGeneratorService boardGeneratorService;
    private final ApplicationUserRepository applicationUserRepository;
    private final GameRepository gameRepository;
    private final GeneralBalanceConfig generalConfig;
    private final GameStartService gameStartService;
    private final AiPlayerConfig aiPlayerConfig;

    public GameGeneratorService(BoardGeneratorService boardGeneratorService, ApplicationUserRepository applicationUserRepository, GameRepository gameRepository, BasicGameTimer basicGameTimer, GeneralBalanceConfig generalConfig, GameStartService gameStartService, AiPlayerConfig aiPlayerConfig) {
        this.boardGeneratorService = boardGeneratorService;
        this.applicationUserRepository = applicationUserRepository;
        this.gameRepository = gameRepository;
        this.generalConfig = generalConfig;
        this.gameStartService = gameStartService;
        this.aiPlayerConfig = aiPlayerConfig;
    }

    public void generateGame(String userId1, String userId2) {
        //noinspection OptionalGetWithoutIsPresent
        var players = Stream.of(userId1, userId2)
                .map(applicationUserRepository::findById)
                .map(Optional::get)
                .map(this::createPlayerFromUser)
                .toList();
        var board = boardGeneratorService.generateBoard(players.get(0), players.get(1));
        var stateManager = new StateManager(
                GameState.CREATED,
                null,
                null,
                null
        );
        var game = new Game(players, board, stateManager);
        game = gameRepository.save(game);
        gameStartService.stateTransition(game);
    }

    public void generateGameAgainstAI(Authentication auth) throws AuthorizationException {
        var player = AuthoritiesUtils.getIdFromAuthorities(auth.getAuthorities())
                .flatMap(applicationUserRepository::findById)
                .map(this::createPlayerFromUser)
                .orElseThrow(() -> new AuthorizationException("Could not recognize user! You must login in order to play against AI."));
        var aiPlayer = createAIPlayer();
        var board = boardGeneratorService.generateBoard(player, aiPlayer);
        var stateManager = createStateManager();

        aiPlayer.setIsReady(true); // AI is ready by default - does not need any time to wait

        // start just like a regular game
        var players = List.of(player, aiPlayer);
        var game = new Game(players, board, stateManager);
        game = gameRepository.save(game);
        gameStartService.stateTransition(game);
    }

    private StateManager createStateManager() {
        return new StateManager(
                GameState.CREATED,
                null,
                null,
                null
        );
    }

    private Player createAIPlayer() {
        return new Player(
                aiPlayerConfig.getId(),
                aiPlayerConfig.getName(),
                new ResourceSet(
                        generalConfig.getStartingResources(),
                        generalConfig.getStartingResources(),
                        generalConfig.getStartingResources()
                ),
                300
        );
    }

    private Player createPlayerFromUser(ApplicationUser user) {
        return new Player(
                user.getId(),
                user.getNickname(),
                new ResourceSet(
                        generalConfig.getStartingResources(),
                        generalConfig.getStartingResources(),
                        generalConfig.getStartingResources()
                ),
                user.getRating()
        );
    }
}
