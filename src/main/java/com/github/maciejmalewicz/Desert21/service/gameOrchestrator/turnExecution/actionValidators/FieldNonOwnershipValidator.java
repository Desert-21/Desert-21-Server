package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.FieldNonOwnershipValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldNonOwnershipValidator implements ActionValidator<FieldNonOwnershipValidatable> {

    @Override
    public boolean validate(List<FieldNonOwnershipValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(FieldNonOwnershipValidatable validatable) {
        var field = validatable.field();
        var player = validatable.player();
        return !player.getId().equals(field.getOwnerId());
    }
}
