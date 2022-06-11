package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.EnoughUnitsValidatable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class EnoughUnitsValidator implements ActionValidator<EnoughUnitsValidatable> {

    @Override
    public boolean validate(List<EnoughUnitsValidatable> validatables, TurnExecutionContext context) {
        var groupedByFields = validatables.stream()
                .collect(groupingBy(EnoughUnitsValidatable::field));
        return groupedByFields.entrySet().stream().allMatch(this::validate);
    }

    private boolean validate(Map.Entry<Field, List<EnoughUnitsValidatable>> entry) {
        var field = entry.getKey();
        var validatables = entry.getValue();
        var troopsNeeded = validatables.stream()
                .map(EnoughUnitsValidatable::army)
                .reduce(new Army(0, 0, 0), Army::combineWith);
        var troopsOnField = field.getArmy();
        return troopsOnField.getDroids() >= troopsNeeded.getDroids()
                && troopsOnField.getTanks() >= troopsNeeded.getTanks()
                && troopsOnField.getCannons() >= troopsNeeded.getCannons();
    }
}
