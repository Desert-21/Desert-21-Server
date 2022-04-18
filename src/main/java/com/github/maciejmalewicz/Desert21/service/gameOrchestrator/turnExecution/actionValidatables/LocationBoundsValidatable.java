package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;


import com.github.maciejmalewicz.Desert21.models.Location;

public record LocationBoundsValidatable(Location location) implements ActionValidatable {
}
