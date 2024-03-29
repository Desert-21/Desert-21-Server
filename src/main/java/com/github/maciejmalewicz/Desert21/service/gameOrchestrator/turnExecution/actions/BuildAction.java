package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.BuildBuildingCostCalculator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.fieldAtLocation;
import static com.github.maciejmalewicz.Desert21.utils.BuildingUtils.buildingTypeToConfig;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildAction implements Action {
    @NonNull
    private Location location;
    @NonNull
    private BuildingType buildingType;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        var buildingMaterialsCost = BuildBuildingCostCalculator.getBuildingCost(buildingType, context.gameBalance());
        var costValidatable = new CostValidatable(new ResourceSet(0, buildingMaterialsCost, 0));

        var field = fieldAtLocation(context.game().getFields(), location);
        var fieldOwnershipValidatable = new FieldOwnershipValidatable(field, context.player());

        var isFieldEmptyValidatable = new IsFieldEmptyValidatable(field);

        var hasUpgradeRequiredToBuildValidatable = new HasUpgradeRequiredToBuildValidatable(buildingType);

        var isBuildingBuildableValidatable = new IsBuildingBuildableValidatable(buildingType);

        var singleBuildPerLocationValidatable = new SingleBuildPerLocationValidatable(location);

        var buildingCapValidatable = getCapValidatable();

        return List.of(
                costValidatable,
                fieldOwnershipValidatable,
                isFieldEmptyValidatable,
                hasUpgradeRequiredToBuildValidatable,
                isBuildingBuildableValidatable,
                singleBuildPerLocationValidatable,
                buildingCapValidatable
        );
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var buildingMaterialsCost = BuildBuildingCostCalculator.getBuildingCost(buildingType, context.gameBalance());
        var paymentEvent = new PaymentEvent(new ResourceSet(0, buildingMaterialsCost, 0));

        var buildBuildingEvent = new BuildBuildingEvent(location, buildingType);

        return List.of(paymentEvent, buildBuildingEvent);
    }

    private ActionValidatable getCapValidatable() {
        return buildingType == BuildingType.TOWER ?
                new MaxTowersCapValidatable() :
                new MaxFactoriesCapValidatable();
    }
}
