package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsFieldEmptyValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IsFieldEmptyValidator implements ActionValidator<IsFieldEmptyValidatable> {

    @Override
    public boolean validate(List<IsFieldEmptyValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream()
                .allMatch(field -> BuildingType.EMPTY_FIELD == field.field().getBuilding().getType());
    }
}
