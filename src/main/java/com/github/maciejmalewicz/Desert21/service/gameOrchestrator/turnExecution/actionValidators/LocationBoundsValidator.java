package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LocationBoundsValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationBoundsValidator implements ActionValidator<LocationBoundsValidatable> {

    @Override
    public boolean validate(List<LocationBoundsValidatable> validatables, TurnExecutionContext context) {
        var fields = context.game().getFields();
        return validatables.stream()
                .map(LocationBoundsValidatable::location)
                .allMatch(location -> BoardUtils.isWithinBoardBounds(fields, location));
    }
}
