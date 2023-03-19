package com.github.maciejmalewicz.Desert21.ai.actionsPickers;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.ActionPossibilityType;
import com.github.maciejmalewicz.Desert21.ai.actionsGetters.MetalActionsGetter;
import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.ai.helpers.FieldEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.parameters.TrainActionParameters;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.TrainActionRatingEngine;
import com.github.maciejmalewicz.Desert21.ai.ratingEngines.WaitActionRatingEngine;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.TrainAction;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MetalActionsPicker implements ActionsPicker {

    private final MetalActionsGetter metalActionsGetter;
    private final TrainActionRatingEngine trainActionRatingEngine;
    private final WaitActionRatingEngine waitActionRatingEngine;

    public MetalActionsPicker(MetalActionsGetter metalActionsGetter, TrainActionRatingEngine trainActionRatingEngine, WaitActionRatingEngine waitActionRatingEngine) {
        this.metalActionsGetter = metalActionsGetter;
        this.trainActionRatingEngine = trainActionRatingEngine;
        this.waitActionRatingEngine = waitActionRatingEngine;
    }

    @Override
    public List<Action> getActions(AiTurnExecutionContext context) {
        var pickedActions = pickActionsInLoop(context);
        return pickedActions.stream()
                .map(actionPossibility -> (TrainActionParameters)actionPossibility.getParameters())
                .map(this::parametersToAction)
                .toList();
    }

    private Action parametersToAction(TrainActionParameters trainActionParameters) {
        return new TrainAction(
                trainActionParameters.getLocation(),
                trainActionParameters.getUnitType(),
                trainActionParameters.getTrainingMode()
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
            preExecuteTrainingAction((TrainActionParameters) pickedAction.getParameters(), context);
        } while(true);
        return allSelectedActions;
    }

    private void preExecuteTrainingAction(TrainActionParameters actionParameters, AiTurnExecutionContext context) {
        try {
            // mark that we can't train here anymore
            var field = (FieldEnhancementWrapper) BoardUtils.fieldAtLocation(
                    context.game().getFields(),
                    actionParameters.getLocation()
            );
            field.setAlreadyTrainingHere(true);

            // mark that we can't use that metal anymore
            var cost = actionParameters.getMetalCost();
            var lockedResources = context.game().getLockedResources();
            var newLockedResources = lockedResources.add(new ResourceSet(cost, 0, 0));
            context.game().setLockedResources(newLockedResources);
        } catch (NotAcceptableException e) {
            throw new RuntimeException();
        }
    }

    private ActionPossibility<?> pickSingleAction(AiTurnExecutionContext context) {
        var actionPossibilities = metalActionsGetter.getActions(
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
        var type = actionPossibility.getActionPossibilityType();
        if (type == ActionPossibilityType.TRAIN) {
            return trainActionRatingEngine.rateAction((TrainActionParameters) actionPossibility.getParameters());
        }
        if (type == ActionPossibilityType.WAIT) {
            return waitActionRatingEngine.rateAction(null);
        }
        return 0;
    }
}
