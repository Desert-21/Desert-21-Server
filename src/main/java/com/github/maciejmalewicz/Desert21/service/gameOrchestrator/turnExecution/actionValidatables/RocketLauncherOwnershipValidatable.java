package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.Field;

import java.util.List;

public record RocketLauncherOwnershipValidatable(List<Field> ownedFields) implements ActionValidatable {
}
