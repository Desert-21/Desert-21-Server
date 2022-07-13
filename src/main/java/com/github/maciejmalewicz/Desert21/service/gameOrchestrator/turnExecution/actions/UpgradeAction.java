package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeAction implements Action {
    @NonNull
    private Location location;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        var locationBounds = new LocationBoundsValidatable(location);

        var fields = context.game().getFields();
        var field = BoardUtils.fieldAtLocation(fields, location);
        var fieldOwnership = new FieldOwnershipValidatable(field, context.player());

        var building = field.getBuilding();
        var buildingUpgradable = new BuildingUpgradableValidatable(building);

        var singleUpgradePerLocation = new SingleUpgradePerLocationValidatable(location);

        var buildingConfig = BuildingUtils
                .buildingTypeToConfig(building.getType(), context.gameBalance());

        var nextLevel = building.getLevel() + 1;
        var buildingMaterialsCost = buildingConfig.costAtLevel(nextLevel);
        var cost = new CostValidatable(new ResourceSet(0, buildingMaterialsCost, 0));

        return List.of(
                locationBounds,
                fieldOwnership,
                buildingUpgradable,
                singleUpgradePerLocation,
                cost
        );
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var fields = context.game().getFields();
        var field = BoardUtils.fieldAtLocation(fields, location);
        var building = field.getBuilding();
        var buildingConfig = BuildingUtils
                .buildingTypeToConfig(building.getType(), context.gameBalance());
        var nextLevel = building.getLevel() + 1;
        var buildingMaterialsCost = buildingConfig.costAtLevel(nextLevel);
        var payment = new PaymentEvent(new ResourceSet(0, buildingMaterialsCost, 0));
        var upgrade = new BuildingUpgradeEvent(location);
        return List.of(payment, upgrade);
    }
}
