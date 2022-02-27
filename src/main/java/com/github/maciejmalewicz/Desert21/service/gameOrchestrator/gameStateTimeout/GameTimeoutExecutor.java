package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.gameStateTimeout;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;

public abstract class GameTimeoutExecutor {

    protected abstract long getExecutionOffset();
    protected abstract Notifiable switchGameState(Game game);

    protected final GameRepository gameRepository;
    protected final PlayersNotifier playersNotifier;
    protected final TimeoutsPool timeoutsPool;

    protected GameTimeoutExecutor(GameRepository gameRepository, PlayersNotifier playersNotifier, TimeoutsPool timeoutsPool) {
        this.gameRepository = gameRepository;
        this.playersNotifier = playersNotifier;
        this.timeoutsPool = timeoutsPool;
    }

    public void executeTimeout(GameStateTimeout gameStateTimeout) {
        timeoutsPool.addTimeoutIfAbsent(gameStateTimeout);
        var thread = new Thread(() -> {
            var msToTimeout = DateUtils.millisecondsTo(gameStateTimeout.timeout());
            var msToTimeoutWithOffset = msToTimeout + getExecutionOffset();
            try {
                Thread.sleep(msToTimeoutWithOffset);
            } catch (InterruptedException exc) {
                //ignore
            }
            var gameOptional = gameRepository.findById(gameStateTimeout.gameId());
            if (gameOptional.isEmpty()) {
                return;
            }
            var game = gameOptional.get();
            var timeoutId = game.getStateManager().getCurrentStateTimeoutId();
            if (!timeoutId.equals(gameStateTimeout.timeoutId())) {
                return;
            }
            var notifiable = switchGameState(game);
            gameRepository.save(game);
            timeoutsPool.removeTimeout(gameStateTimeout.gameId());
            playersNotifier.notifyPlayers(game, notifiable);
        });
        thread.start();
    }
}
