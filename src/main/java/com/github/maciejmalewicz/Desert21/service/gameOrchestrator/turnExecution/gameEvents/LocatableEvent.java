package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.models.Location;

public interface LocatableEvent {
    Location getLocation();
}
