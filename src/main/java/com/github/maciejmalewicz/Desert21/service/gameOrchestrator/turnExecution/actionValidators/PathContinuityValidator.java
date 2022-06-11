package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.PathContinuityValidatable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.maciejmalewicz.Desert21.utils.LocationUtils.areNeighbours;

@Service
public class PathContinuityValidator implements ActionValidator<PathContinuityValidatable> {
    @Override
    public boolean validate(List<PathContinuityValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(PathContinuityValidatable validatable) {
        var locations = validatable.path();
        return IntStream.range(0, locations.size() - 1).mapToObj(i -> {
            var first = locations.get(i);
            var second = locations.get(i+1);
            return areNeighbours(first, second);
        }).allMatch(a -> a);
    }
}
