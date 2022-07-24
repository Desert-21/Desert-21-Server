package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.HasSufficientLabForBuildingUpgradeValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HasSufficientLabForBuildingUpgradeValidator implements ActionValidator<HasSufficientLabForBuildingUpgradeValidatable> {

    @Override
    public boolean validate(List<HasSufficientLabForBuildingUpgradeValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(v -> validateSingle(v, context));
    }

    private boolean validateSingle(HasSufficientLabForBuildingUpgradeValidatable validatable, TurnExecutionContext context) {
        try {
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), validatable.location());
            var buildingType = field.getBuilding().getType();
            var isDefensive = buildingType == BuildingType.TOWER || buildingType == BuildingType.HOME_BASE;
            var isLevel3 = field.getBuilding().getLevel() == 3;
            if (!isDefensive || !isLevel3) {
                return true;
            }
            return context.player().ownsUpgrade(LabUpgrade.THE_GREAT_FORTRESS);
        } catch (NotAcceptableException e) {
            return false;
        }
    }
}
