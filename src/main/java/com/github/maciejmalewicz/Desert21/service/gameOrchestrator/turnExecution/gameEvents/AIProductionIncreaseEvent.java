package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AIProductionIncreaseEvent extends GameEvent {

    public AIProductionIncreaseEvent(int turnsToExecute) {
        super(turnsToExecute);
    }
}
