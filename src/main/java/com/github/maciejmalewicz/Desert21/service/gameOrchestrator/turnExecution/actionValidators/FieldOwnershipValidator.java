package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.FieldOwnershipValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldOwnershipValidator implements ActionValidator<FieldOwnershipValidatable> {

    @Override
    public boolean validate(List<FieldOwnershipValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(FieldOwnershipValidatable validatable) {
        var field = validatable.field();
        var player = validatable.player();
        return player.getId().equals(field.getOwnerId());
    }
}
