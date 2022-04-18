package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;

public record CostValidatable(ResourceSet resourceSet) implements ActionValidatable {
}
