package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.ActionPossibilityType;
import com.github.maciejmalewicz.Desert21.ai.helpers.FieldEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.helpers.GameEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.parameters.BuildBuildingActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.UpgradeBuildingActionParameters;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.LocatedField;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.BuildBuildingCostCalculator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.UpgradeBuildingCostCalculator;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Build building
// Upgrade building
@Service
public class BuildingMaterialsActionsGetter implements CategoryActionsGetter {

    @Override
    public List<ActionPossibility<?>> getActions(GameEnhancementWrapper game, Player player, GameBalanceDto gameBalance) {
        var ownedLocatedFields = BoardUtils.boardToOwnedLocatedFieldList(game.getFields(), player.getId());
        var upgradeActions = getUpgradeActions(ownedLocatedFields, player, gameBalance, game);
        var buildActions = getBuildingActions(ownedLocatedFields, player, gameBalance, game);
        return Stream.concat(upgradeActions.stream(), buildActions.stream()).collect(Collectors.toList());
    }

    private List<ActionPossibility<BuildBuildingActionParameters>> getBuildingActions(List<LocatedField> ownedLocatedFields, Player player, GameBalanceDto gameBalance, GameEnhancementWrapper game) {
        var buildableBuildingTypes = getBuildableBuildingTypes(player, gameBalance, game);
        return ownedLocatedFields.stream()
                .filter(locatedField -> locatedField.field().getBuilding().getType() == BuildingType.EMPTY_FIELD)
                .filter(locatedField -> !((FieldEnhancementWrapper) locatedField.field()).isAlreadyBuildingHere())
                .flatMap(locatedField -> buildableBuildingTypes.stream().map(buildingType -> fieldToBuildAction(locatedField, buildingType, gameBalance)))
                .toList();
    }

    private ActionPossibility<BuildBuildingActionParameters> fieldToBuildAction(LocatedField locatedField, BuildingType buildingType, GameBalanceDto gameBalance) {
        var parameters = new BuildBuildingActionParameters(
                locatedField.location(),
                buildingType,
                BuildBuildingCostCalculator.getBuildingCost(buildingType, gameBalance)
        );
        return new ActionPossibility<>(
                ActionPossibilityType.BUILD,
                parameters
        );
    }

    // Includes cost calculation
    private List<BuildingType> getBuildableBuildingTypes(Player player, GameBalanceDto gameBalance, GameEnhancementWrapper game) {
        var playersBuildingMaterials = player.getResources().subtract(game.getLockedResources()).getBuildingMaterials();
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

    private List<ActionPossibility<UpgradeBuildingActionParameters>> getUpgradeActions(List<LocatedField> ownedLocatedFields, Player player, GameBalanceDto gameBalance, GameEnhancementWrapper game) {
        var playersBuildingMaterials = player.getResources().subtract(game.getLockedResources()).getBuildingMaterials();
        return ownedLocatedFields.stream()
                .filter(locatedField -> isBuildingUpgradableByLevel(locatedField.field().getBuilding(), player))
                .filter(locatedField -> !((FieldEnhancementWrapper) locatedField.field()).isAlreadyUpgradingHere())
                .map(locatedField -> fieldToUpgradeAction(locatedField, gameBalance))
                .filter(actionPossibility -> playersBuildingMaterials >= actionPossibility.getParameters().getBuildingMaterialsCost())
                .toList();
    }

    private ActionPossibility<UpgradeBuildingActionParameters> fieldToUpgradeAction(LocatedField locatedField, GameBalanceDto gameBalance) {
        var parameters = new UpgradeBuildingActionParameters(
                locatedField.location(),
                locatedField.field().getBuilding().getType(),
                UpgradeBuildingCostCalculator.getUpgradeCost(locatedField.field().getBuilding(), gameBalance),
                locatedField.field().getBuilding().getLevel() + 1
        );
        return new ActionPossibility<>(
                ActionPossibilityType.UPGRADE,
                parameters
        );
    }

    // doesn't include cost
    private boolean isBuildingUpgradableByLevel(Building building, Player player) {
        if (List.of(BuildingType.EMPTY_FIELD, BuildingType.ROCKET_LAUNCHER).contains(building.getType())) {
            return false;
        }
        if (building.getLevel() > 3) {
            return false;
        }
        return !building.isDefensive() || building.getLevel() != 3 || player.ownsUpgrade(LabUpgrade.THE_GREAT_FORTRESS);
    }
}
