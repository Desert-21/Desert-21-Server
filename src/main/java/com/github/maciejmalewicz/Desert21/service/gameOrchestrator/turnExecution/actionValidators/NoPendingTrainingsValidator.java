package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.NoPendingTrainingsValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoPendingTrainingsValidator implements ActionValidator<NoPendingTrainingsValidatable> {

    @Override
    public boolean validate(List<NoPendingTrainingsValidatable> validatables, TurnExecutionContext context) {
        var forbiddenLocations = context.game().getEventQueue().stream()
                .filter(gameEvent -> gameEvent instanceof ArmyTrainingEvent)
                .map(ArmyTrainingEvent.class::cast)
                .map(ArmyTrainingEvent::getLocation)
                .collect(Collectors.toSet());
        return validatables.stream()
                .map(NoPendingTrainingsValidatable::location)
                .noneMatch(forbiddenLocations::contains);
    }
}
