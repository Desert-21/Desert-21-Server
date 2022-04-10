package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FirstTurnStartService extends StateTransitionService {

    private final BasicGameTimer gameTimer;

    @Autowired
    public FirstTurnStartService(PlayersNotifier playersNotifier, TimeoutExecutor timeoutExecutor, GameRepository gameRepository, BasicGameTimer gameTimer) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.gameTimer = gameTimer;
    }

    @Override
    protected Notifiable getNotifications(Game game) {
        var notification = new NextTurnNotification(game.getStateManager().getCurrentPlayerId(), new Date());
        return new Notifiable() {
            @Override
            public List<Notification<?>> forBoth() {
                return List.of(new Notification<>("START_GAME", notification));
            }
        };
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
        stateManager.setCurrentPlayerId(player.getId());
        return game;
    }
}
