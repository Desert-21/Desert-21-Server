package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables.GameStartTimeoutExecutable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables.TimeoutExecutable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TimeoutExecutablePicker {

    private final GameStartTimeoutExecutable gameStartTimeoutExecutable;

    public TimeoutExecutablePicker(@Lazy GameStartTimeoutExecutable gameStartTimeoutExecutable) {
        this.gameStartTimeoutExecutable = gameStartTimeoutExecutable;
    }

    public TimeoutExecutable pickTimeoutExecutable(Game game) {
        var state = game.getStateManager().getGameState();
        return switch(state) {
            case CREATED -> gameStartTimeoutExecutable;
            case AWAITING -> gameStartTimeoutExecutable;
            case WAITING_TO_START -> gameStartTimeoutExecutable;
            case RESOLVED -> gameStartTimeoutExecutable;
            case FINISHED -> gameStartTimeoutExecutable;
        };
    }
}
