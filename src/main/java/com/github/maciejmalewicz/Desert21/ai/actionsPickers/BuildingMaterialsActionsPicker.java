package com.github.maciejmalewicz.Desert21.ai.actionsPickers;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.ActionPossibilityType;
import com.github.maciejmalewicz.Desert21.ai.actionsGetters.BuildingMaterialsActionsGetter;
import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.ai.helpers.FieldEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.parameters.BuildBuildingActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.UpgradeBuildingActionParameters;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.BuildBuildingActionRatingEngine;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.UpgradeBuildingActionRatingEngine;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.WaitActionRatingEngine;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.BuildAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.UpgradeAction;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BuildingMaterialsActionsPicker implements ActionsPicker {

    private final BuildingMaterialsActionsGetter buildingMaterialsActionsGetter;
    private final BuildBuildingActionRatingEngine buildBuildingActionRatingEngine;
    private final UpgradeBuildingActionRatingEngine upgradeBuildingActionRatingEngine;
    private final WaitActionRatingEngine waitActionRatingEngine;

    public BuildingMaterialsActionsPicker(BuildingMaterialsActionsGetter buildingMaterialsActionsGetter, BuildBuildingActionRatingEngine buildBuildingActionRatingEngine, UpgradeBuildingActionRatingEngine upgradeBuildingActionRatingEngine, WaitActionRatingEngine waitActionRatingEngine) {
        this.buildingMaterialsActionsGetter = buildingMaterialsActionsGetter;
        this.buildBuildingActionRatingEngine = buildBuildingActionRatingEngine;
        this.upgradeBuildingActionRatingEngine = upgradeBuildingActionRatingEngine;
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
        var actionType = actionPossibility.getActionPossibilityType();
        if (actionType == ActionPossibilityType.BUILD) {
            var buildParameters = (BuildBuildingActionParameters) actionPossibility.getParameters();
            return new BuildAction(
                    buildParameters.getLocation(),
                    buildParameters.getBuildingType()
            );
        }
        var upgradeParameters = (UpgradeBuildingActionParameters) actionPossibility.getParameters();
        return new UpgradeAction(
                upgradeParameters.getLocation()
        );

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
        if (actionType == ActionPossibilityType.BUILD) {
            preExecuteBuildAction((BuildBuildingActionParameters) actionPossibility.getParameters(), context);
        }
        if (actionType == ActionPossibilityType.UPGRADE) {
            preExecuteUpgradeAction((UpgradeBuildingActionParameters) actionPossibility.getParameters(), context);
        }
    }

    private void preExecuteUpgradeAction(UpgradeBuildingActionParameters parameters, AiTurnExecutionContext context) {
        try {
            // marking that we can't upgrade anymore
            var field = (FieldEnhancementWrapper) BoardUtils.fieldAtLocation(
                    context.game().getFields(),
                    parameters.getLocation()
            );
            field.setAlreadyUpgradingHere(true);

            //marking that we used the resources
            var lockedResources = context.game().getLockedResources();
            context.game().setLockedResources(lockedResources.add(
                    new ResourceSet(0, parameters.getBuildingMaterialsCost(), 0)
            ));
        } catch (NotAcceptableException e) {
            throw new RuntimeException();
        }
    }

    private void preExecuteBuildAction(BuildBuildingActionParameters parameters, AiTurnExecutionContext context) {
        try {
            // marking that we can't build here anymore
            var field = (FieldEnhancementWrapper) BoardUtils.fieldAtLocation(
                    context.game().getFields(),
                    parameters.getLocation()
            );
            field.setAlreadyBuildingHere(true);

            //marking that we used the resources
            var lockedResources = context.game().getLockedResources();
            context.game().setLockedResources(lockedResources.add(
                    new ResourceSet(0, parameters.getBuildingMaterialsCost(), 0)
            ));
        } catch (NotAcceptableException e) {
            throw new RuntimeException();
        }
    }

    private ActionPossibility<?> pickSingleAction(AiTurnExecutionContext context) {
        var actionPossibilities = buildingMaterialsActionsGetter.getActions(
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
            case BUILD -> buildBuildingActionRatingEngine.rateAction((BuildBuildingActionParameters) actionPossibility.getParameters());
            case UPGRADE -> upgradeBuildingActionRatingEngine.rateAction((UpgradeBuildingActionParameters) actionPossibility.getParameters());
            case WAIT -> waitActionRatingEngine.rateAction((WaitActionRatingEngine) actionPossibility.getParameters());
            default -> 0;
        };
    }
}
