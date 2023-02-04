package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.LocatedField;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.FireRocketAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.LabAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.RocketCostCalculator;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.LabUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Upgrade lab
// Fire rocket
public class ElectricityActionsGetter implements CategoryActionsGetter<Integer> {

    @Override
    public List<ActionPossibility<Integer>> getActions(Game game, Player player, GameBalanceDto gameBalance) {
        return Stream.concat(
                getLabActions(player, gameBalance).stream(),
                getRocketStrikeActions(game, player, gameBalance).stream()
        ).toList();
    }

    private List<ActionPossibility<Integer>> getRocketStrikeActions(Game game, Player player, GameBalanceDto gameBalance) {
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
        return BoardUtils.boardToEnemyLocatedFieldList(game.getFields(), player.getId()).stream()
                .flatMap(locatedField -> fieldToRocketStrikeActions(locatedField, player, rocketStrikeCost).stream())
                .toList();
    }

    private List<ActionPossibility<Integer>> fieldToRocketStrikeActions(LocatedField locatedField, Player player, int rocketStrikeCost) {
        if (!player.ownsUpgrade(LabUpgrade.SUPER_SONIC_ROCKETS)) {
            return List.of(new ActionPossibility<>(new FireRocketAction(locatedField.location(), false), rocketStrikeCost));
        }
        return List.of(
                new ActionPossibility<>(new FireRocketAction(locatedField.location(), false), rocketStrikeCost),
                new ActionPossibility<>(new FireRocketAction(locatedField.location(), true), rocketStrikeCost)
        );
    }

    private List<ActionPossibility<Integer>> getLabActions(Player player, GameBalanceDto gameBalance) {
        var labBranches = List.of(
                gameBalance.upgrades().combat(),
                gameBalance.upgrades().control(),
                gameBalance.upgrades().production()
        );
        return labBranches.stream()
                .flatMap(branch -> getUnlockedLabUpgradesOfBranch(branch, player).stream())
                .filter(upgrade -> !player.ownsUpgrade(upgrade))
                .map(upgrade -> upgradeToLabActionPossibility(upgrade, gameBalance))
                .toList();
    }

    private ActionPossibility<Integer> upgradeToLabActionPossibility(LabUpgrade labUpgrade, GameBalanceDto gameBalance) {
        int cost;
        try {
            cost = LabUtils.getUpgradeCost(labUpgrade, gameBalance);
        } catch (NotAcceptableException e) {
            cost = -1;
        }
        return new ActionPossibility<>(new LabAction(labUpgrade), cost);
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


