package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.NextTurnNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.NextTurnTransitionService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.stateTransitionServices.StateTransitionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.config.Constants.NEXT_TURN_NOTIFICATION;

@Service
public class NextTurnTimeoutExecutable implements TimeoutExecutable {

    private final NextTurnTransitionService nextTurnTransitionService;

    public NextTurnTimeoutExecutable(NextTurnTransitionService nextTurnTransitionService) {
        this.nextTurnTransitionService = nextTurnTransitionService;
    }

    @Override
    public Optional<Notification<?>> getNotifications(Game game) {
        return Optional.empty();
    }

    @Override
    public StateTransitionService getStateTransitionService(Game game) {
        return nextTurnTransitionService;
    }

    @Override
    public long getExecutionOffset() {
        return 2000;
    }
}
