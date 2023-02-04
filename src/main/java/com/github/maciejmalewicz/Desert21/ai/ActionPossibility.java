package com.github.maciejmalewicz.Desert21.ai;

import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ActionPossibility<Cost> {
    private Action action;
    private Cost cost;
}
