package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.GameArchivingService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameArchivingTimeoutExecutable implements TimeoutExecutable {

    private final GameArchivingService gameArchivingService;

    public GameArchivingTimeoutExecutable(GameArchivingService gameArchivingService) {
        this.gameArchivingService = gameArchivingService;
    }

    @Override
    public Optional<Notification<?>> getNotifications(Game game) {
        return Optional.empty();
    }

    @Override
    public StateTransitionService getStateTransitionService(Game game) {
        return gameArchivingService;
    }

    @Override
    public long getExecutionOffset() {
        return 0;
    }
}
