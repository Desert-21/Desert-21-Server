package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.SurrenderNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.github.maciejmalewicz.Desert21.config.Constants.SURRENDER_NOTIFICATION;

@Service
public class SurrenderService {

    private final GamePlayerService gamePlayerService;
    private final GameRepository gameRepository;
    private final TimeoutExecutor timeoutExecutor;
    private final PlayersNotifier playersNotifier;

    public SurrenderService(GamePlayerService gamePlayerService, GameRepository gameRepository, TimeoutExecutor timeoutExecutor, PlayersNotifier playersNotifier) {
        this.gamePlayerService = gamePlayerService;
        this.gameRepository = gameRepository;
        this.timeoutExecutor = timeoutExecutor;
        this.playersNotifier = playersNotifier;
    }

    public void surrender(Authentication auth, String gameId) throws NotAcceptableException, AuthorizationException {
        var data = gamePlayerService.getGamePlayerData(gameId, auth);
        var player = data.player();
        var game = data.game();

        //handle new timeout
        var stateManager = game.getStateManager();
        var opponent = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(player.getId()))
                .findFirst()
                .orElseThrow();
        var toWait = 60_000;
        var executionDate = DateUtils.millisecondsFromNow(toWait);
        stateManager.setTimeout(executionDate);
        stateManager.setCurrentStateTimeoutId(UUID.randomUUID().toString());
        stateManager.setWinnerId(opponent.getId());
        stateManager.setGameState(GameState.FINISHED);

        var savedGame = gameRepository.save(game);
        timeoutExecutor.executeTimeoutOnGame(savedGame);

        playersNotifier.notifyPlayers(game, new Notification<>(SURRENDER_NOTIFICATION, new SurrenderNotification(player.getId())));
    }
}
