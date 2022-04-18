package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GameEvent {
    protected int turnsToExecute;

    public void nextTurn() {
        --turnsToExecute;
    }

    public boolean shouldTriggerNow() {
        return turnsToExecute == -1;
    }
}
