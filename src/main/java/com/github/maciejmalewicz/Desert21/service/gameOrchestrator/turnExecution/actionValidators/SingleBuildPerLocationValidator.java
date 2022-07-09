package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleBuildPerLocationValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SingleBuildPerLocationValidator implements ActionValidator<SingleBuildPerLocationValidatable> {

    @Override
    public boolean validate(List<SingleBuildPerLocationValidatable> validatables, TurnExecutionContext context) {
        return validatables.size() == validatables.stream()
                .map(SingleBuildPerLocationValidatable::location)
                .distinct()
                .count();
    }
}
