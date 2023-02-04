package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.LocatedField;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.BuildAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.UpgradeAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.BuildBuildingCostCalculator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.UpgradeBuildingCostCalculator;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Build building
// Upgrade building
public class BuildingMaterialsActionsGetter implements CategoryActionsGetter<Integer> {

    @Override
    public List<ActionPossibility<Integer>> getActions(Game game, Player player, GameBalanceDto gameBalance) {
        var ownedLocatedFields = BoardUtils.boardToOwnedLocatedFieldList(game.getFields(), player.getId());
        var upgradeActions = getUpgradeActions(ownedLocatedFields, player, gameBalance);
        var buildActions = getBuildingActions(ownedLocatedFields, player, gameBalance);
        return Stream.concat(upgradeActions.stream(), buildActions.stream()).toList();
    }

    private List<ActionPossibility<Integer>> getBuildingActions(List<LocatedField> ownedLocatedFields, Player player, GameBalanceDto gameBalance) {
        var buildableBuildingTypes = getBuildableBuildingTypes(player, gameBalance);
        return ownedLocatedFields.stream()
                .filter(locatedField -> locatedField.field().getBuilding().getType() == BuildingType.EMPTY_FIELD)
                .flatMap(locatedField -> buildableBuildingTypes.stream().map(buildingType -> fieldToBuildAction(locatedField, buildingType, gameBalance)))
                .toList();
    }

    private ActionPossibility<Integer> fieldToBuildAction(LocatedField locatedField, BuildingType buildingType, GameBalanceDto gameBalance) {
        return new ActionPossibility<>(
                new BuildAction(locatedField.location(), buildingType),
                BuildBuildingCostCalculator.getBuildingCost(buildingType, gameBalance)
        );
    }

    // Includes cost calculation
    private List<BuildingType> getBuildableBuildingTypes(Player player, GameBalanceDto gameBalance) {
        var playersBuildingMaterials = player.getResources().getBuildingMaterials();
        var buildingTypeAcc = new ArrayList<BuildingType>();
        if (player.ownsUpgrade(LabUpgrade. TOWER_CREATOR)) {
            buildingTypeAcc.add(BuildingType.TOWER);
        }
        if (player.ownsUpgrade(LabUpgrade.FACTORY_BUILDERS)) {
            buildingTypeAcc.addAll(List.of(BuildingType.METAL_FACTORY, BuildingType.BUILDING_MATERIALS_FACTORY, BuildingType.ELECTRICITY_FACTORY));
        }
        return buildingTypeAcc.stream()
                .filter(buildingType -> playersBuildingMaterials >= BuildBuildingCostCalculator.getBuildingCost(buildingType, gameBalance))
                .toList();
    }

    private List<ActionPossibility<Integer>> getUpgradeActions(List<LocatedField> ownedLocatedFields, Player player, GameBalanceDto gameBalance) {
        var playersBuildingMaterials = player.getResources().getBuildingMaterials();
        return ownedLocatedFields.stream()
                .filter(locatedField -> isBuildingUpgradableByLevel(locatedField.field().getBuilding(), player))
                .map(locatedField -> fieldToUpgradeAction(locatedField, gameBalance))
                .filter(actionPossibility -> playersBuildingMaterials >= actionPossibility.getCost())
                .toList();
    }

    private ActionPossibility<Integer> fieldToUpgradeAction(LocatedField locatedField, GameBalanceDto gameBalance) {
        return new ActionPossibility<>(
                new UpgradeAction(locatedField.location()),
                UpgradeBuildingCostCalculator.getUpgradeCost(locatedField.field().getBuilding(), gameBalance)
        );
    }

    // doesn't include cost
    private boolean isBuildingUpgradableByLevel(Building building, Player player) {
        if (List.of(BuildingType.EMPTY_FIELD, BuildingType.ROCKET_LAUNCHER).contains(building.getType()))
        if (building.getLevel() > 3) {
            return false;
        }
        return !building.isDefensive() || building.getLevel() != 3 || player.ownsUpgrade(LabUpgrade.THE_GREAT_FORTRESS);
    }
}
