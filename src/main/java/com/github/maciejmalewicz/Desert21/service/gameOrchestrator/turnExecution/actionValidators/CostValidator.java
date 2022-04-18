package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.CostValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostValidator implements ActionValidator<CostValidatable> {

    @Override
    public boolean validate(List<CostValidatable> validatables, TurnExecutionContext context) {
        var costSum = validatables.stream()
                .map(CostValidatable::resourceSet)
                .reduce(ResourceSet::add)
                .orElse(new ResourceSet(0, 0, 0));
        var playersResources = context.player().getResources();
        var remainingResources = playersResources.subtract(costSum);
        return remainingResources.isNonNegative();
    }
}
