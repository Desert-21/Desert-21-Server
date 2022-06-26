package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.EnoughUnitsValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class EnoughUnitsValidator implements ActionValidator<EnoughUnitsValidatable> {

    @Override
    public boolean validate(List<EnoughUnitsValidatable> validatables, TurnExecutionContext context) {
        var groupedByFields = validatables.stream()
                .collect(groupingBy(EnoughUnitsValidatable::location));
        return groupedByFields.entrySet().stream().allMatch(entry -> validate(entry, context));
    }

    private boolean validate(Map.Entry<Location, List<EnoughUnitsValidatable>> entry, TurnExecutionContext context) {
        var location = entry.getKey();
        var validatables = entry.getValue();
        var troopsNeeded = validatables.stream()
                .map(EnoughUnitsValidatable::army)
                .reduce(new Army(0, 0, 0), Army::combineWith);
        try {
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), location);
            var troopsOnField = field.getArmy();
            return troopsOnField.getDroids() >= troopsNeeded.getDroids()
                    && troopsOnField.getTanks() >= troopsNeeded.getTanks()
                    && troopsOnField.getCannons() >= troopsNeeded.getCannons();
        } catch (NotAcceptableException e) {
            return false;
        }

    }
}
