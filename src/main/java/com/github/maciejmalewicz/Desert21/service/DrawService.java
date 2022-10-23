package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.game.DrawAction;
import com.github.maciejmalewicz.Desert21.exceptions.AuthorizationException;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;

@Service
public class DrawService {

    private final static int DRAW_REQUEST_MIN_TIME_WINDOW = 60_000;

    private final GamePlayerService gamePlayerService;
    private final PlayersNotifier playersNotifier;
    private final GameRepository gameRepository;
    private final TimeoutExecutor timeoutExecutor;

    public DrawService(GamePlayerService gamePlayerService, PlayersNotifier playersNotifier, GameRepository gameRepository, TimeoutExecutor timeoutExecutor) {
        this.gamePlayerService = gamePlayerService;
        this.playersNotifier = playersNotifier;
        this.gameRepository = gameRepository;
        this.timeoutExecutor = timeoutExecutor;
    }

    @Transactional(rollbackFor = Exception.class)
    public void draw(String gameId, Authentication authentication, DrawAction action) throws AuthorizationException, NotAcceptableException {
        var gamePlayerData = gamePlayerService.getGamePlayerData(gameId, authentication);
        var game = gamePlayerData.game();
        var player = gamePlayerData.player();
        var opponent = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(player.getId()))
                .findFirst()
                .orElseThrow();
        Runnable saveGame = () -> gameRepository.save(game);

        if (action == DrawAction.REQUEST) {
            requestDraw(player, opponent, saveGame);
            return;
        }

        if (action == DrawAction.CANCEL) {
            cancelDraw(player, saveGame);
            return;
        }

        executeOfferResponse(action, game, player, opponent, saveGame);
    }

    private void requestDraw(Player player, Player opponent, Runnable saveGame) throws NotAcceptableException {
        if (isDrawTimeoutForbidden(player)) {
            throw new NotAcceptableException("Cannot request another draw!");
        }
        player.setDrawOfferDisabledTimeout(DateUtils.millisecondsFromNow(DRAW_REQUEST_MIN_TIME_WINDOW));
        player.setOfferingDraw(true);

        saveGame.run();
        playersNotifier.notifyPlayer(opponent.getId(), new Notification<>(DRAW_REQUESTED_NOTIFICATION, player.getId()));
    }

    private void cancelDraw(Player player, Runnable saveGame) {
        player.setOfferingDraw(false);
        saveGame.run();
    }

    private boolean isDrawTimeoutForbidden(Player player) {
        var timeout = player.getDrawOfferDisabledTimeout();
        return timeout != null && new Date().before(timeout);
    }

    private void executeOfferResponse(DrawAction drawAction, Game game, Player player, Player opponent, Runnable saveGame) throws NotAcceptableException {
        if (!opponent.isOfferingDraw()) {
            throw new NotAcceptableException("Opponent has cancelled their draw request!");
        }
        if (drawAction == DrawAction.REJECT) {
            executeDrawRejection(player, opponent, saveGame);
        } else {
            executeDrawAcceptance(game);
        }
    }

    private void executeDrawRejection(Player player, Player opponent, Runnable saveGame) {
        opponent.setOfferingDraw(false);
        playersNotifier.notifyPlayer(opponent.getId(), new Notification<>(DRAW_REJECTED_NOTIFICATION, player.getId()));
        saveGame.run();
    }

    private void executeDrawAcceptance(Game game) {
        var stateManager = game.getStateManager();
        var toWait = 10_000;
        var executionDate = DateUtils.millisecondsFromNow(toWait);
        stateManager.setTimeout(executionDate);
        stateManager.setCurrentStateTimeoutId(UUID.randomUUID().toString());
        stateManager.setWinnerId(null);
        stateManager.setGameState(GameState.FINISHED);

        var savedGame = gameRepository.save(game);
        timeoutExecutor.executeTimeoutOnGame(savedGame);

        playersNotifier.notifyPlayers(game, new Notification<>(DRAW_ACCEPTED_NOTIFICATION, null));
    }
}
