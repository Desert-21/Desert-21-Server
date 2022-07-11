package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsFieldEnemyValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IsFieldEnemyValidator implements ActionValidator<IsFieldEnemyValidatable> {

    @Override
    public boolean validate(List<IsFieldEnemyValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(this::validateSingle);
    }

    private boolean validateSingle(IsFieldEnemyValidatable isFieldEnemyValidatable) {
        var ownerId = isFieldEnemyValidatable.field().getOwnerId();
        if (ownerId == null) {
            return false;
        }
        return !isFieldEnemyValidatable.currentPLayer().getId().equals(ownerId);
    }
}
