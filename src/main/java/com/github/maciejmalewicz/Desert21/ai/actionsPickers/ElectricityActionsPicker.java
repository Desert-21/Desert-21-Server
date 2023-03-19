package com.github.maciejmalewicz.Desert21.ai.actionsPickers;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.ActionPossibilityType;
import com.github.maciejmalewicz.Desert21.ai.actionsGetters.ElectricityActionsGetter;
import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.ai.parameters.FireRocketActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.LabActionParameters;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.FireRocketActionRatingEngine;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.LabActionRatingEngine;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.WaitActionRatingEngine;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.FireRocketAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.LabAction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ElectricityActionsPicker implements ActionsPicker {

    private final ElectricityActionsGetter electricityActionsGetter;
    private final FireRocketActionRatingEngine fireRocketActionRatingEngine;
    private final LabActionRatingEngine labActionRatingEngine;
    private final WaitActionRatingEngine waitActionRatingEngine;

    public ElectricityActionsPicker(ElectricityActionsGetter electricityActionsGetter, FireRocketActionRatingEngine fireRocketActionRatingEngine, LabActionRatingEngine labActionRatingEngine, WaitActionRatingEngine waitActionRatingEngine) {
        this.electricityActionsGetter = electricityActionsGetter;
        this.fireRocketActionRatingEngine = fireRocketActionRatingEngine;
        this.labActionRatingEngine = labActionRatingEngine;
        this.waitActionRatingEngine = waitActionRatingEngine;
    }

    @Override
    public List<Action> getActions(AiTurnExecutionContext context) {
        var pickedActions = pickActionsInLoop(context);
        return pickedActions.stream()
                .map(this::possibilityToAction)
                .toList();
    }

    private Action possibilityToAction(ActionPossibility<?> actionPossibility) {
        if (actionPossibility.getActionPossibilityType() == ActionPossibilityType.FIRE_ROCKET) {
            var parameters = (FireRocketActionParameters) actionPossibility.getParameters();
            return new FireRocketAction(
                    parameters.getLocation(),
                    parameters.isTargetingRocket()
            );
        }
        var parameters = (LabActionParameters) actionPossibility.getParameters();
        return new LabAction(parameters.getLabUpgrade());
    }

    private List<ActionPossibility<?>> pickActionsInLoop(AiTurnExecutionContext context) {
        var allSelectedActions = new ArrayList<ActionPossibility<?>>();
        ActionPossibility<?> pickedAction;
        do {
            pickedAction = pickSingleAction(context);
            if (pickedAction.getActionPossibilityType() == ActionPossibilityType.WAIT) {
                break;
            }
            allSelectedActions.add(pickedAction);
            preExecuteAction(pickedAction, context);
        } while(true);
        return allSelectedActions;
    }

    private void preExecuteAction(ActionPossibility<?> actionPossibility, AiTurnExecutionContext context) {
        var actionType = actionPossibility.getActionPossibilityType();
        if (actionType == ActionPossibilityType.FIRE_ROCKET) {
            preExecuteFireRocketAction((FireRocketActionParameters) actionPossibility.getParameters(), context);
        }
        if (actionType == ActionPossibilityType.LAB_EVENT) {
            preExecuteLabAction((LabActionParameters) actionPossibility.getParameters(), context);
        }
    }

    private void preExecuteFireRocketAction(FireRocketActionParameters parameters, AiTurnExecutionContext context) {
        // marking that we can't fire rocket anymore
        context.game().setRocketAlreadyFired(true);

        // marking that we use electricity
        var lockedResources = context.game().getLockedResources();
        context.game().setLockedResources(lockedResources.add(new ResourceSet(0, 0, parameters.getElectricityCost())));
    }

    private void preExecuteLabAction(LabActionParameters parameters, AiTurnExecutionContext context) {
        // adding the upgrade to the list of upgraded in that turn
        context.game().getCurrentTurnUpgrades().add(parameters.getLabUpgrade());

        // marking that we use electricity
        var lockedResources = context.game().getLockedResources();
        context.game().setLockedResources(lockedResources.add(new ResourceSet(0, 0, parameters.getElectricityCost())));
    }

    private ActionPossibility<?> pickSingleAction(AiTurnExecutionContext context) {
        var actionPossibilities = electricityActionsGetter.getActions(
                context.game(),
                context.player(),
                context.gameBalance()
        );
        actionPossibilities.add(new ActionPossibility<>(ActionPossibilityType.WAIT, null));
        return actionPossibilities.stream()
                .max(Comparator.comparingDouble(this::rateAction))
                .orElseThrow();
    }

    private double rateAction(ActionPossibility<?> actionPossibility) {
        return switch (actionPossibility.getActionPossibilityType()) {
            case FIRE_ROCKET -> fireRocketActionRatingEngine.rateAction((FireRocketActionParameters) actionPossibility.getParameters());
            case LAB_EVENT -> labActionRatingEngine.rateAction((LabActionParameters) actionPossibility.getParameters());
            case WAIT -> waitActionRatingEngine.rateAction((WaitActionRatingEngine) actionPossibility.getParameters());
            default -> 0;
        };
    }
}
