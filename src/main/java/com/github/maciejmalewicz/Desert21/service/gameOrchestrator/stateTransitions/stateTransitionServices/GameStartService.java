package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.StartGameNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.START_GAME_NOTIFICATION;

@Service
public class GameStartService extends StateTransitionService {

    private final BasicGameTimer gameTimer;

    public GameStartService(PlayersNotifier playersNotifier,
                            TimeoutExecutor timeoutExecutor,
                            GameRepository gameRepository,
                            BasicGameTimer gameTimer) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.gameTimer = gameTimer;
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        return Optional.of(PlayersNotificationPair.forBoth(
                new Notification<>(
                        START_GAME_NOTIFICATION,
                        new StartGameNotification(game.getId())
                )
        ));
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        return gameTimer.getInitialTime();
    }

    @Override
    protected Game changeGameState(Game game) {
        var stateManager = game.getStateManager();
        stateManager.setGameState(GameState.WAITING_TO_START);
        return game;
    }
}
