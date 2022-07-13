package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.LabUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.LabUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.utils.LabUtils.getUpgradeCost;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabAction implements Action {

    @NonNull
    private LabUpgrade upgrade;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        var electricityCost = getUpgradeCost(upgrade, context.gameBalance());
        var costValidatable = new CostValidatable(new ResourceSet(0, 0, electricityCost));

        var singleUpgradePerBranchValidatable = new SingleUpgradePerBranchValidatable(upgrade);

        var labUpgradeHierarchyValidatable = new LabUpgradeHierarchyValidatable(upgrade);

        var labUpgradeNotRepeatedValidatable = new LabUpgradeNotRepeatedValidatable(upgrade);

        return List.of(
                costValidatable,
                singleUpgradePerBranchValidatable,
                labUpgradeHierarchyValidatable,
                labUpgradeNotRepeatedValidatable
        );
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var labEvent = new LabUpgradeEvent(upgrade);

        var electricityCost = getUpgradeCost(upgrade, context.gameBalance());
        var paymentEvent = new PaymentEvent(new ResourceSet(0, 0, electricityCost));

        return List.of(labEvent, paymentEvent);
    }
}
