package com.github.maciejmalewicz.Desert21.ai.actionsPickers;

import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArmyActionsPicker implements ActionsPicker {
    @Override
    public List<Action> getActions(AiTurnExecutionContext turnExecutionContext) {
        return new ArrayList<>();
    }
}
