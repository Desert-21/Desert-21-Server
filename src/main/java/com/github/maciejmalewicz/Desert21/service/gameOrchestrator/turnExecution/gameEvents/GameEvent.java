package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
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
