package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.RocketStrikeValidRocketStrikeTargetValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.RocketCostCalculator.isRocketLauncher;

@Service
public class RocketStrikeValidRocketStrikeTargetValidator implements ActionValidator<RocketStrikeValidRocketStrikeTargetValidatable> {

    @Override
    public boolean validate(List<RocketStrikeValidRocketStrikeTargetValidatable> validatables, TurnExecutionContext context) {
        var optionalValidatable = validatables.stream().findFirst();
        if (optionalValidatable.isEmpty()) {
            return true;
        }
        var validatable = optionalValidatable.get();
        if (!validatable.isAttackingRocket()) {
            return true;
        }
        var location = validatable.location();
        try {
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), location);
            return isRocketLauncher(field);
        } catch (NotAcceptableException e) {
            return false;
        }
    }
}
