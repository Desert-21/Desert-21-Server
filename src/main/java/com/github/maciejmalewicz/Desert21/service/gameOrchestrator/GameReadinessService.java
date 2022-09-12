package com.github.maciejmalewicz.Desert21.service.gameOrchestrator;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.FirstTurnStartService;
import com.github.maciejmalewicz.Desert21.utils.AuthoritiesUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameReadinessService {

    private final GameRepository gameRepository;
    private final FirstTurnStartService firstTurnStartService;
    private final GamePlayerService gamePlayerService;

    public GameReadinessService(GameRepository gameRepository, FirstTurnStartService firstTurnStartService, GamePlayerService gamePlayerService) {
        this.gameRepository = gameRepository;
        this.firstTurnStartService = firstTurnStartService;
        this.gamePlayerService = gamePlayerService;
    }

    @Transactional
    public void notifyAboutReadiness(Authentication authentication, String gameId) throws NotAcceptableException, AuthorizationException {
        var gamePlayer = gamePlayerService.getGamePlayerData(gameId, authentication);
        var game = gamePlayer.game();
        var player = gamePlayer.player();

        if (!game.getStateManager().getGameState().equals(GameState.WAITING_TO_START)) {
            throw new NotAcceptableException("Readiness checkout phase has already passed!");
        }

        player.setIsReady(true);

        if (isEveryoneReady(game)) {
            //start game
            firstTurnStartService.stateTransition(game);
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
