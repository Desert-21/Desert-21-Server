package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.PathFromAndToConvergenceValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathFromAndToConvergenceValidator implements ActionValidator<PathFromAndToConvergenceValidatable> {

    @Override
    public boolean validate(List<PathFromAndToConvergenceValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(PathFromAndToConvergenceValidatable validatable) {
        var path = validatable.path();
        if (path.size() < 1) {
            return false;
        }
        var first = path.get(0);
        var last = path.get(validatable.path().size() - 1);
        return first.equals(validatable.from()) && last.equals(validatable.to());
    }
}
