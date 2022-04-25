package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.TurnResolutionPhaseStartService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResolutionPhaseTimeoutExecutable implements TimeoutExecutable {

    private final TurnResolutionPhaseStartService turnResolutionPhaseStartService;

    public ResolutionPhaseTimeoutExecutable(TurnResolutionPhaseStartService turnResolutionPhaseStartService) {
        this.turnResolutionPhaseStartService = turnResolutionPhaseStartService;
    }

    @Override
    public Optional<Notification<?>> getNotifications(Game game) {
        return Optional.empty();  //empty, since players will be notified by the service and no need to be done twice
    }

    @Override
    public StateTransitionService getStateTransitionService(Game game) {
        return turnResolutionPhaseStartService;
    }

    @Override
    public long getExecutionOffset() {
        return 0;
    }
}
