package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.GameState;
import com.github.maciejmalewicz.Desert21.repository.GameRepository;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.PlayersNotifier;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.ResolutionPhaseNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.TimeoutExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOLUTION_PHASE_NOTIFICATION;

@Service
public class TurnResolutionPhaseStartService extends StateTransitionService {

    public TurnResolutionPhaseStartService(PlayersNotifier playersNotifier,
                                              TimeoutExecutor timeoutExecutor,
                                              GameRepository gameRepository) {
        super(playersNotifier, timeoutExecutor, gameRepository);
    }

    @Override
    protected Notifiable getNotifications(Game game) {
        return new Notifiable() {
            @Override
            public List<Notification<?>> forBoth() {
                return List.of(
                        new Notification<>(
                                RESOLUTION_PHASE_NOTIFICATION,
                                new ResolutionPhaseNotification(game.getStateManager().getTimeout())
                        )
                );
            }
        };
    }

    @Override
    protected long getTimeToWaitForTimeout(Game game) {
        return 5_000;
    }

    @Override
    protected Game changeGameState(Game game) {
        var stateManger = game.getStateManager();
        stateManger.setGameState(GameState.RESOLVED);
        return game;
    }
}
