package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.RocketStrikeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.RocketCostCalculator.calculateRocketCost;
import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.boardToOwnedFieldList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireRocketAction implements Action {
    @NonNull
    private Location target;
    @NonNull
    @JsonProperty(value="isTargetingRocket")
    private Boolean isTargetingRocket;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        var singleRocketStrikePerTurnValidatable = new SingleRocketStrikePerTurnValidatable();

        var ownedFields = boardToOwnedFieldList(context.game().getFields(), context.player().getId());
        var electricityCost = calculateRocketCost(context.gameBalance(), context.player(), ownedFields);
        var costValidatable = new CostValidatable(new ResourceSet(0, 0, electricityCost));

        var rocketLauncherOwnershipValidatable = new RocketLauncherOwnershipValidatable(ownedFields);

        var isFieldTargetableByRocketValidatable = new IsFieldTargetableByRocketValidatable(target);

        var validTargetValidatable = new RocketStrikeValidRocketStrikeTargetValidatable(target, isTargetingRocket);

        var superSonicUpgradeValidatable = new SuperSonicUpgradeValidatable(isTargetingRocket);
        return List.of(
                singleRocketStrikePerTurnValidatable,
                costValidatable,
                rocketLauncherOwnershipValidatable,
                isFieldTargetableByRocketValidatable,
                validTargetValidatable,
                superSonicUpgradeValidatable
        );
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var ownedFields = boardToOwnedFieldList(context.game().getFields(), context.player().getId());
        var electricityCost = calculateRocketCost(context.gameBalance(), context.player(), ownedFields);
        var paymentEvent = new PaymentEvent(new ResourceSet(0, 0, electricityCost));

        var rocketStrikeEvent = new RocketStrikeEvent(target, isTargetingRocket);

        return List.of(paymentEvent, rocketStrikeEvent);
    }
}
