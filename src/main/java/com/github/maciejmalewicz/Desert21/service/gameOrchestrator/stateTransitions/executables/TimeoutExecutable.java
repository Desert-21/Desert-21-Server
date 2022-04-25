package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;

import java.util.Optional;

public interface TimeoutExecutable {

    Optional<Notification<?>> getNotifications(Game game);
    StateTransitionService getStateTransitionService(Game game);
    long getExecutionOffset();
}

