package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.UpgradeBuildingCostCalculator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.tomcat.util.http.parser.Upgrade;

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

        var buildingMaterialsCost = UpgradeBuildingCostCalculator.getUpgradeCost(building, context.gameBalance());
        if (buildingMaterialsCost == -1) {
            throw new NotAcceptableException("Empty field is does not have a config!");
        }
        var cost = new CostValidatable(new ResourceSet(0, buildingMaterialsCost, 0));

        var hasSufficientLab = new HasSufficientLabForBuildingUpgradeValidatable(location);

        return List.of(
                locationBounds,
                fieldOwnership,
                buildingUpgradable,
                singleUpgradePerLocation,
                cost,
                hasSufficientLab
        );
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var fields = context.game().getFields();
        var field = BoardUtils.fieldAtLocation(fields, location);
        var building = field.getBuilding();
        var buildingMaterialsCost = UpgradeBuildingCostCalculator.getUpgradeCost(building, context.gameBalance());
        if (buildingMaterialsCost == -1) {
            throw new NotAcceptableException("Empty field is does not have a config!");
        }
        var payment = new PaymentEvent(new ResourceSet(0, buildingMaterialsCost, 0));
        var upgrade = new BuildingUpgradeEvent(location);
        return List.of(payment, upgrade);
    }
}
