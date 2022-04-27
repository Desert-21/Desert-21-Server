package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;

import java.util.Optional;
import java.util.UUID;

public abstract class StateTransitionService {

    protected abstract Optional<PlayersNotificationPair> getNotifications(Game game);
    protected abstract long getTimeToWaitForTimeout(Game game);
    protected abstract Game changeGameState(Game game);

    protected final PlayersNotifier playersNotifier;
    protected final TimeoutExecutor timeoutExecutor;
    protected final GameRepository gameRepository;

    protected StateTransitionService(PlayersNotifier playersNotifier, TimeoutExecutor timeoutExecutor, GameRepository gameRepository) {
        this.playersNotifier = playersNotifier;
        this.timeoutExecutor = timeoutExecutor;
        this.gameRepository = gameRepository;
    }

    public void stateTransition(Game gameBefore) {
        //change game state if necessary
        var game = changeGameState(gameBefore);

        //handle new timeout
        var toWait = getTimeToWaitForTimeout(game);
        var executionDate = DateUtils.millisecondsFromNow(toWait);
        game.getStateManager().setTimeout(executionDate);
        game.getStateManager().setCurrentStateTimeoutId(UUID.randomUUID().toString());
        gameRepository.save(game);
        timeoutExecutor.executeTimeoutOnGame(game);

        //handle notifications
        var notifiable = getNotifications(game);
        notifiable.ifPresent(notification -> playersNotifier.notifyPlayers(game, notification));
    }
}
