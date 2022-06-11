package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.models.Location;

import java.util.List;

public record PathFromAndToConvergenceValidatable(List<Location> path, Location from, Location to) implements ActionValidatable {
}
