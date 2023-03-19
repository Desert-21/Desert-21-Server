package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.ActionPossibilityType;
import com.github.maciejmalewicz.Desert21.ai.helpers.GameEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.parameters.FireRocketActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.LabActionParameters;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.LocatedField;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.RocketCostCalculator;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.LabUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Upgrade lab
// Fire rocket
@Service
public class ElectricityActionsGetter implements CategoryActionsGetter {

    @Override
    public List<ActionPossibility<?>> getActions(GameEnhancementWrapper game, Player player, GameBalanceDto gameBalance) {
        return Stream.concat(
                getLabActions(player, gameBalance, game).stream(),
                getRocketStrikeActions(game, player, gameBalance).stream()
        ).collect(Collectors.toList());
    }

    private List<ActionPossibility<FireRocketActionParameters>> getRocketStrikeActions(GameEnhancementWrapper game, Player player, GameBalanceDto gameBalance) {
        var ownedFields = BoardUtils.boardToOwnedLocatedFieldList(game.getFields(), player.getId());
        var hasNoRockets = ownedFields.stream()
                .noneMatch(locatedField -> locatedField.field().getBuilding().getType().equals(BuildingType.ROCKET_LAUNCHER));
        if (hasNoRockets) {
            return new ArrayList<>();
        }
        var rocketStrikeCost = RocketCostCalculator.calculateRocketCost(
                gameBalance,
                player,
                ownedFields.stream().map(LocatedField::field).toList()
        );
        if (rocketStrikeCost > player.getResources().subtract(game.getLockedResources()).getElectricity()) {
            return new ArrayList<>();
        }
        return BoardUtils.boardToEnemyLocatedFieldList(game.getFields(), player.getId()).stream()
                .filter(this::isNotLevel4Defensive)
                .flatMap(locatedField -> fieldToRocketStrikeActions(locatedField, player, rocketStrikeCost).stream())
                .toList();
    }

    private boolean isNotLevel4Defensive(LocatedField locatedField) {
        var building = locatedField.field().getBuilding();
        return !(building.isDefensive() && building.getLevel() == 4);
    }

    private List<ActionPossibility<FireRocketActionParameters>> fieldToRocketStrikeActions(LocatedField locatedField, Player player, int rocketStrikeCost) {
        if (!player.ownsUpgrade(LabUpgrade.SUPER_SONIC_ROCKETS)) {
            var parameters = new FireRocketActionParameters(
                    rocketStrikeCost,
                    locatedField.location(),
                    false
            );
            return List.of(new ActionPossibility<>(ActionPossibilityType.FIRE_ROCKET, parameters));
        }
        return List.of(
                new ActionPossibility<>(ActionPossibilityType.FIRE_ROCKET, new FireRocketActionParameters(
                        rocketStrikeCost,
                        locatedField.location(),
                        false
                )),
                new ActionPossibility<>(ActionPossibilityType.FIRE_ROCKET, new FireRocketActionParameters(
                        rocketStrikeCost,
                        locatedField.location(),
                        true
                ))
        );
    }

    private List<ActionPossibility<LabActionParameters>> getLabActions(Player player, GameBalanceDto gameBalance, GameEnhancementWrapper game) {
        var labBranches = List.of(
                gameBalance.upgrades().combat(),
                gameBalance.upgrades().control(),
                gameBalance.upgrades().production()
        );
        return labBranches.stream()
                .flatMap(branch -> getUnlockedLabUpgradesOfBranch(branch, player).stream())
                .filter(upgrade -> !player.ownsUpgrade(upgrade))
                .filter(upgrade -> isLockedByOtherCurrentUpgrades(game, gameBalance, upgrade))
                .map(upgrade -> upgradeToLabActionPossibility(upgrade, gameBalance))
                .filter(upgradePossibility -> upgradePossibility.getParameters().getElectricityCost() <= player.getResources().subtract(game.getLockedResources()).getElectricity())
                .toList();
    }

    private boolean isLockedByOtherCurrentUpgrades(GameEnhancementWrapper game, GameBalanceDto gameBalance, LabUpgrade labUpgrade) {
        try {
            var branch = LabUtils.getLabBranchConfig(labUpgrade, gameBalance);
            var isTryingToUpgradeOnTheSameBranch = game.getCurrentTurnUpgrades().stream()
                    .anyMatch(upgrade -> branch.containsUpgrade(labUpgrade));
            return !isTryingToUpgradeOnTheSameBranch;
        } catch (NotAcceptableException e) {
            throw new RuntimeException();
        }
    }

    private ActionPossibility<LabActionParameters> upgradeToLabActionPossibility(LabUpgrade labUpgrade, GameBalanceDto gameBalance) {
        int cost;
        try {
            cost = LabUtils.getUpgradeCost(labUpgrade, gameBalance);
        } catch (NotAcceptableException e) {
            cost = -1;
        }
        var parameters = new LabActionParameters(cost, labUpgrade);
        return new ActionPossibility<>(ActionPossibilityType.LAB_EVENT, parameters);
    }

    private List<LabUpgrade> getUnlockedLabUpgradesOfBranch(LabBranchConfig labBranchConfig, Player player) {
        if (!player.ownsUpgrade(labBranchConfig.getBaseUpgrade())) {
            return List.of(labBranchConfig.getBaseUpgrade());
        }
        if (!player.ownsOneOfUpgrades(labBranchConfig.getFirstTierUpgrades())) {
            return Stream.concat(
                    Stream.of(labBranchConfig.getBaseUpgrade()),
                    labBranchConfig.getFirstTierUpgrades().stream()
            ).toList();
        }
        if (!player.ownsOneOfUpgrades(labBranchConfig.getSecondTierUpgrades())) {
            return Stream.of(
                    Stream.of(labBranchConfig.getBaseUpgrade()),
                    labBranchConfig.getFirstTierUpgrades().stream(),
                    labBranchConfig.getSecondTierUpgrades().stream()

            ).flatMap(s -> s).toList();
        }
        return Stream.of(
                Stream.of(labBranchConfig.getBaseUpgrade()),
                labBranchConfig.getFirstTierUpgrades().stream(),
                labBranchConfig.getSecondTierUpgrades().stream(),
                Stream.of(labBranchConfig.getSuperUpgrade())
        ).flatMap(s -> s).toList();
    }
}


