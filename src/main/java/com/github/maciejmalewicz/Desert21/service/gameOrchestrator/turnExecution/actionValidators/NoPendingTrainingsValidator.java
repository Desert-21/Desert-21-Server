package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.NoPendingTrainingsValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoPendingTrainingsValidator implements ActionValidator<NoPendingTrainingsValidatable> {

    @Override
    public boolean validate(List<NoPendingTrainingsValidatable> validatables, TurnExecutionContext context) {
//        context.game().getEventQueue().stream().filter(e)
//        validatables.stream()
//                .map(NoPendingTrainingsValidatable::location)
//                .
        return true;
    }
}
