package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.repository.ApplicationUserRepository;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameGeneratorService {

    private final BoardGeneratorService boardGeneratorService;
    private final ApplicationUserRepository applicationUserRepository;
    private final GameRepository gameRepository;

    public GameGeneratorService(BoardGeneratorService boardGeneratorService, ApplicationUserRepository applicationUserRepository, GameRepository gameRepository) {
        this.boardGeneratorService = boardGeneratorService;
        this.applicationUserRepository = applicationUserRepository;
        this.gameRepository = gameRepository;
    }

    public Game generateGame(String userId1, String userId2) {
        //noinspection OptionalGetWithoutIsPresent
        var players = Stream.of(userId1, userId2)
                .map(applicationUserRepository::findById)
                .map(Optional::get)
                .map(this::createPlayerFromUser)
                .toList();
        var board = boardGeneratorService.generateBoard(players.get(0), players.get(1));
        var game = new Game(players, board);
        return this.gameRepository.save(game);
    }

    private Player createPlayerFromUser(ApplicationUser user) {
        return new Player(
                user.getId(),
                user.getNickname(),
                new ResourceSet(60, 60, 60)
        );
    }


}
