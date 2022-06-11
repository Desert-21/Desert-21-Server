package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.models.Location;

import java.util.List;

public record PathLengthValidatable(List<Location> path, Army army) implements ActionValidatable {
}
