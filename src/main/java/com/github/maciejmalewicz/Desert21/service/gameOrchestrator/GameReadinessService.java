package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class GameReadinessService {

    private final GameRepository gameRepository;
    private final NextTurnStarterService nextTurnStarterService;

    public GameReadinessService(GameRepository gameRepository, NextTurnStarterService nextTurnStarterService) {
        this.gameRepository = gameRepository;
        this.nextTurnStarterService = nextTurnStarterService;
    }

    public synchronized void notifyAboutReadiness(Authentication authentication, String gameId) throws NotAcceptableException {
        var userId = AuthoritiesUtils.getIdFromAuthorities(authentication.getAuthorities())
                .orElseThrow(() -> new NotAcceptableException("User could not be identified!"));
        var game = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotAcceptableException("Game not found!"));
        var player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotAcceptableException("User is not participating in selected game!"));
        player.setIsReady(true);

        if (isEveryoneReady(game)) {
            //start game
            nextTurnStarterService.startNextTurn(game);
        } else {
            //just memorize that the player has notified their readiness
            gameRepository.save(game);
        }
    }

    private boolean isEveryoneReady(Game game) {
        return game.getPlayers().stream()
                .allMatch(Player::getIsReady);
    }
}
