package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.Field;

public record IsFieldEmptyValidatable(Field field) implements ActionValidatable {
}
