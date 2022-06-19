package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;

@Service
public class FirstTurnStartService extends StateTransitionService {

    private final BasicGameTimer gameTimer;

    @Autowired
    public FirstTurnStartService(PlayersNotifier playersNotifier, TimeoutExecutor timeoutExecutor, GameRepository gameRepository, BasicGameTimer gameTimer) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.gameTimer = gameTimer;
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        var content = new NextTurnNotification(game.getStateManager().getCurrentPlayerId(), new Date());
        return Optional.of(PlayersNotificationPair.forBoth(
                new Notification<>(NEXT_TURN_NOTIFICATION, content))
        );
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        return gameTimer.getMoveTime(game);
    }


    @Override
    protected Game changeGameState(Game game) {
        var stateManager = game.getStateManager();
        var player = game.getPlayers().get(0);
        stateManager.setGameState(GameState.AWAITING);
        stateManager.setFirstPlayerId(player.getId());
        stateManager.setTurnCounter(1);
        stateManager.setCurrentPlayerId(player.getId());
        return game;
    }
}
