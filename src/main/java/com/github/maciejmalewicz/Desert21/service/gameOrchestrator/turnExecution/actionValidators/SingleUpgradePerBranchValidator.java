package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabBranchConfig;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.SingleUpgradePerBranchValidatable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.github.maciejmalewicz.Desert21.utils.LabUtils.getLabBranchConfig;

@Service
public class SingleUpgradePerBranchValidator implements ActionValidator<SingleUpgradePerBranchValidatable> {

    @Override
    public boolean validate(List<SingleUpgradePerBranchValidatable> validatables, TurnExecutionContext context) {
        var optionalLabBranchConfigs = validatables.stream()
                .map(v -> validatableToOptionalBranchConfig(v, context))
                .toList();
        var isAlwaysPresent = optionalLabBranchConfigs.stream().allMatch(Optional::isPresent);
        if (!isAlwaysPresent) {
            return false;
        }
        return optionalLabBranchConfigs.size() == optionalLabBranchConfigs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .count();
    }

    private Optional<LabBranchConfig> validatableToOptionalBranchConfig(SingleUpgradePerBranchValidatable validatable, TurnExecutionContext context) {
        try {
            var branchConfig = getLabBranchConfig(validatable.labUpgrade(), context.gameBalance());
            return Optional.of(branchConfig);
        } catch (NotAcceptableException e) {
            return Optional.empty();
        }
    }
}
