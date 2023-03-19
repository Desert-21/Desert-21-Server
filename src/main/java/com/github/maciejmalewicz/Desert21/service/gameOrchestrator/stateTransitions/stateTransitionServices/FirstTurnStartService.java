package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.config.AiPlayerConfig;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotificationPair;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;

@Service
public class FirstTurnStartService extends StateTransitionService {

    private final BasicGameTimer gameTimer;
    private final AiPlayerConfig aiPlayerConfig;

    @Autowired
    public FirstTurnStartService(PlayersNotifier playersNotifier, TimeoutExecutor timeoutExecutor, GameRepository gameRepository, BasicGameTimer gameTimer, AiPlayerConfig aiPlayerConfig) {
        super(playersNotifier, timeoutExecutor, gameRepository);
        this.gameTimer = gameTimer;
        this.aiPlayerConfig = aiPlayerConfig;
    }

    @Override
    protected Optional<PlayersNotificationPair> getNotifications(Game game) {
        var content = new NextTurnNotification(
                game.getId(),
                game.getStateManager().getCurrentPlayerId(),
                game.getStateManager().getTimeout(),
                game.getStateManager().getTurnCounter()
        );
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

        if (aiPlayerConfig.isAiTurn(game)) {
            stateManager.setGameState(GameState.AWAITING_AI);
        } else {
            stateManager.setGameState(GameState.AWAITING);
        }

        stateManager.setFirstPlayerId(player.getId());
        stateManager.setTurnCounter(1);
        stateManager.setCurrentPlayerId(player.getId());
        return game;
    }
}
