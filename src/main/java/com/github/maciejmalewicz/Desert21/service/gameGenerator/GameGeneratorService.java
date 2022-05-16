package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralBalanceConfig;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.GameStartService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameGeneratorService {

    private final BoardGeneratorService boardGeneratorService;
    private final ApplicationUserRepository applicationUserRepository;
    private final GameRepository gameRepository;
    private final BasicGameTimer basicGameTimer;
    private final GeneralBalanceConfig generalConfig;
    private final GameStartService gameStartService;

    public GameGeneratorService(BoardGeneratorService boardGeneratorService, ApplicationUserRepository applicationUserRepository, GameRepository gameRepository, BasicGameTimer basicGameTimer, GeneralBalanceConfig generalConfig, GameStartService gameStartService) {
        this.boardGeneratorService = boardGeneratorService;
        this.applicationUserRepository = applicationUserRepository;
        this.gameRepository = gameRepository;
        this.basicGameTimer = basicGameTimer;
        this.generalConfig = generalConfig;
        this.gameStartService = gameStartService;
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
}
