package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults;

import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.GameTimer;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.Notifiable;

public interface EventResult extends Notifiable {
    long millisecondsToView();
}
