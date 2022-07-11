package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;

public record IsFieldEnemyValidatable(Field field, Player currentPLayer) implements ActionValidatable {
}
