package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.LabUpgradeHierarchyValidatable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.utils.LabUtils.getLabBranchConfig;

@Service
public class LabUpgradeHierarchyValidator implements ActionValidator<LabUpgradeHierarchyValidatable> {

    @Override
    public boolean validate(List<LabUpgradeHierarchyValidatable> validatables, TurnExecutionContext context) {
        return validatables.stream().allMatch(v -> validateSingle(v, context.gameBalance(), context.player()));
    }

    private boolean validateSingle(LabUpgradeHierarchyValidatable validatable, GameBalanceDto gameBalanceDto, Player player) {
        var upgrade = validatable.labUpgrade();
        try {
            var branchConfig = getLabBranchConfig(upgrade, gameBalanceDto);
            return branchConfig.isAllowedToUpgrade(upgrade, player.getOwnedUpgrades());
        } catch (NotAcceptableException e) {
            return false;
        }
    }
}
