package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsFieldTargetableByRocketValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IsFieldTargetableByRocketValidator implements ActionValidator<IsFieldTargetableByRocketValidatable> {

    @Override
    public boolean validate(List<IsFieldTargetableByRocketValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(v -> validateSingle(v, context));
    }

    private boolean validateSingle(IsFieldTargetableByRocketValidatable validatable, TurnExecutionContext context) {
        try {
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), validatable.location());
            var building = field.getBuilding();
            var hasImmunity = building.isDefensive() && building.getLevel() == 4;
            return !hasImmunity;
        } catch (NotAcceptableException e) {
            return false;
        }
    }
}
