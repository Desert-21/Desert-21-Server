package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.BasicGameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.GameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.FirstTurnStartService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameStartTimeoutExecutable implements TimeoutExecutable {

    private final GameTimer gameTimer;
    private final FirstTurnStartService firstTurnStartService;

    public GameStartTimeoutExecutable(BasicGameTimer gameTimer, FirstTurnStartService firstTurnStartService) {
        this.gameTimer = gameTimer;
        this.firstTurnStartService = firstTurnStartService;
    }

    @Override
    public Optional<Notification<?>> getNotifications(Game game) {
        return Optional.of(new Notification<>("PREPARATION_PHASE_TIMEOUT", null));
    }

    @Override
    public StateTransitionService getStateTransitionService(Game game) {
        return firstTurnStartService;
    }

    @Override
    public long getExecutionOffset() {
        return gameTimer.getInitialTime();
    }
}
