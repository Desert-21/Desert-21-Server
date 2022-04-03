package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.misc.balance.GeneralConfig;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.gameStateTimeout.GameStartTimeoutExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.gameStateTimeout.GameStateTimeout;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class GameGeneratorService {

    private final BoardGeneratorService boardGeneratorService;
    private final ApplicationUserRepository applicationUserRepository;
    private final GameRepository gameRepository;
    private final BasicGameTimer basicGameTimer;
    private final GameStartTimeoutExecutor gameStartTimeoutExecutor;
    private final GeneralConfig generalConfig;

    public GameGeneratorService(BoardGeneratorService boardGeneratorService, ApplicationUserRepository applicationUserRepository, GameRepository gameRepository, BasicGameTimer basicGameTimer, GameStartTimeoutExecutor gameStartTimeoutExecutor, GeneralConfig generalConfig) {
        this.boardGeneratorService = boardGeneratorService;
        this.applicationUserRepository = applicationUserRepository;
        this.gameRepository = gameRepository;
        this.basicGameTimer = basicGameTimer;
        this.gameStartTimeoutExecutor = gameStartTimeoutExecutor;
        this.generalConfig = generalConfig;
    }

    public Game generateGame(String userId1, String userId2) {
        //noinspection OptionalGetWithoutIsPresent
        var players = Stream.of(userId1, userId2)
                .map(applicationUserRepository::findById)
                .map(Optional::get)
                .map(this::createPlayerFromUser)
                .toList();
        var board = boardGeneratorService.generateBoard(players.get(0), players.get(1));
        var stateManager = new StateManager(
                GameState.WAITING_TO_START,
                DateUtils.millisecondsFromNow(basicGameTimer.getInitialTime()),
                null,
                null
        );
        var game = new Game(players, board, stateManager);
        var savedGame = gameRepository.save(game);
        var timeout = generateGameStartTimeout(game);
        savedGame.getStateManager().setCurrentStateTimeoutId(timeout.timeoutId());
        savedGame = gameRepository.save(savedGame);
        gameStartTimeoutExecutor.executeTimeout(timeout);
        return savedGame;
    }

    private Player createPlayerFromUser(ApplicationUser user) {
        return new Player(
                user.getId(),
                user.getNickname(),
                new ResourceSet(
                        generalConfig.getStartingResources(),
                        generalConfig.getStartingResources(),
                        generalConfig.getStartingResources()
                )
        );
    }

    private GameStateTimeout generateGameStartTimeout(Game game) {
        return new GameStateTimeout(
                UUID.randomUUID().toString(),
                DateUtils.millisecondsFromNow(basicGameTimer.getInitialTime()),
                game.getId()
        );
    }


}
