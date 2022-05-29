package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleTrainingPerLocationValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SingleTrainingPerLocationValidator implements ActionValidator<SingleTrainingPerLocationValidatable> {

    @Override
    public boolean validate(List<SingleTrainingPerLocationValidatable> validatables, TurnExecutionContext context) {
        return validatables.size() == validatables.stream()
                .map(SingleTrainingPerLocationValidatable::location)
                .distinct()
                .count();
    }
}
