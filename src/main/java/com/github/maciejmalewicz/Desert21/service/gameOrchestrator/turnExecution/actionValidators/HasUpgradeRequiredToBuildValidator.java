package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.HasUpgradeRequiredToBuildValidatable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HasUpgradeRequiredToBuildValidator implements ActionValidator<HasUpgradeRequiredToBuildValidatable> {

    @Override
    public boolean validate(List<HasUpgradeRequiredToBuildValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(v -> validateSingle(v, context));
    }

    private boolean validateSingle(HasUpgradeRequiredToBuildValidatable validatable, TurnExecutionContext context) {
        var buildingType = validatable.buildingType();
        var upgradeRequiredOpt = getLabUpgradeRequired(buildingType);
        if (upgradeRequiredOpt.isEmpty()) {
            return true;
        }
        var upgradeRequired = upgradeRequiredOpt.get();
        return context.player().ownsUpgrade(upgradeRequired);
    }

    private Optional<LabUpgrade> getLabUpgradeRequired(BuildingType buildingType) {
        return switch (buildingType) {
            case TOWER -> Optional.of(LabUpgrade.TOWER_CREATOR);
            case METAL_FACTORY, BUILDING_MATERIALS_FACTORY, ELECTRICITY_FACTORY -> Optional.of(LabUpgrade.FACTORY_BUILDERS);
            default -> Optional.empty();
        };
    }
}
