package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.GameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameStartTimeoutExecutable implements TimeoutExecutable {

    private final GameTimer gameTimer;

    public GameStartTimeoutExecutable(BasicGameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }

    @Override
    public Notifiable getNotifications(Game game) {
        return new Notifiable() {
            @Override
            public List<Notification<?>> forBoth() {
                return List.of(new Notification<>("PREPARATION_PHASE_TIMEOUT", null));
            }
        };
    }

    @Override
    public StateTransitionService getStateTransitionService(Game game) {
        return null;
    }

    @Override
    public long getExecutionOffset() {
        return 0;
    }
}
