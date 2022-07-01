package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleRocketStrikePerTurnValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SingleRocketStrikePerTurnValidator implements ActionValidator<SingleRocketStrikePerTurnValidatable> {
    @Override
    public boolean validate(List<SingleRocketStrikePerTurnValidatable> validatables, TurnExecutionContext context) {
        return validatables.size() == 1 || validatables.size() == 0;
    }
}
