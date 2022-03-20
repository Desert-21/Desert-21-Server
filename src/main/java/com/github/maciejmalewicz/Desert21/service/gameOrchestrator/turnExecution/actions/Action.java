package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators.ActionValidator;

import java.util.ArrayList;
import java.util.List;

public interface Action {
    default List<ActionValidator> getActionValidators() {
        return new ArrayList<>();
    }
}
