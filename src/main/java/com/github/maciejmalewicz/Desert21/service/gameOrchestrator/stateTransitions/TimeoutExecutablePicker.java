package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions;

import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.stateTransitions.executables.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TimeoutExecutablePicker {

    private final GameStartTimeoutExecutable gameStartTimeoutExecutable;
    private final NextTurnTimeoutExecutable nextTurnTimeoutExecutable;
    private final ResolutionPhaseTimeoutExecutable resolutionPhaseTimeoutExecutable;
    private final GameArchivingTimeoutExecutable gameArchivingTimeoutExecutable;

    public TimeoutExecutablePicker(@Lazy GameStartTimeoutExecutable gameStartTimeoutExecutable,
                                   @Lazy NextTurnTimeoutExecutable nextTurnTimeoutExecutable,
                                   @Lazy ResolutionPhaseTimeoutExecutable resolutionPhaseTimeoutExecutable,
                                   @Lazy GameArchivingTimeoutExecutable gameArchivingTimeoutExecutable) {
        this.gameStartTimeoutExecutable = gameStartTimeoutExecutable;
        this.nextTurnTimeoutExecutable = nextTurnTimeoutExecutable;
        this.resolutionPhaseTimeoutExecutable = resolutionPhaseTimeoutExecutable;
        this.gameArchivingTimeoutExecutable = gameArchivingTimeoutExecutable;
    }

    public TimeoutExecutable pickTimeoutExecutable(Game game) {
        var state = game.getStateManager().getGameState();
        return switch(state) {
            case CREATED, WAITING_TO_START -> gameStartTimeoutExecutable;
            case AWAITING -> resolutionPhaseTimeoutExecutable;
            case RESOLVED -> nextTurnTimeoutExecutable;
            case FINISHED -> gameArchivingTimeoutExecutable;
        };
    }
}
