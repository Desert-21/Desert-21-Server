package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ActionValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PlayersActionsValidatingService {

    private final List<ActionValidator<?>> actionValidators;

    public PlayersActionsValidatingService(
            BuildingUpgradableValidator buildingUpgradableValidator,
            CostValidator costValidator,
            FieldOwnershipValidator fieldOwnershipValidator,
            LocationBoundsValidator locationBoundsValidator,
            SingleUpgradePerLocationValidator singleUpgradePerLocationValidator,
            BuildingSufficientForUnitsTrainingValidator buildingSufficientForUnitsTrainingValidator,
            NoPendingTrainingsValidator noPendingTrainingsValidator,
            ProductionTypeUpgradesOwnershipValidator productionTypeUpgradesOwnershipValidator,
            SingleTrainingPerLocationValidator singleTrainingPerLocationValidator

    ) {
        actionValidators = List.of(
                locationBoundsValidator,
                fieldOwnershipValidator,
                singleUpgradePerLocationValidator,
                buildingUpgradableValidator,
                costValidator,
                singleTrainingPerLocationValidator,
                buildingSufficientForUnitsTrainingValidator,
                productionTypeUpgradesOwnershipValidator,
                noPendingTrainingsValidator
        );
    }

    public boolean validatePlayersActions(Collection<Action> actions, TurnExecutionContext context) {
        var validatables = new ArrayList<ActionValidatable>();
        try {
            for (Action action: actions) {
                var validatablesFromAction = action.getActionValidatables(context);
                validatables.addAll(validatablesFromAction);
            }
        } catch (NotAcceptableException exc) {
            return false;
        }
        return actionValidators.stream()
                .allMatch(validator -> executeSingleValidation(validator, validatables, context));
    }

    private <T extends ActionValidatable> boolean executeSingleValidation(
            ActionValidator<T> validator,
            List<ActionValidatable> validatables,
            TurnExecutionContext context
    ) {
        var selectedValidatables = validatables.stream()
                .filter(v -> v.getClass().equals(validator.getValidatableClass()))
                .map(validator.getValidatableClass()::cast)
                .toList();
        return validator.validate(selectedValidatables, context);
    }
}
