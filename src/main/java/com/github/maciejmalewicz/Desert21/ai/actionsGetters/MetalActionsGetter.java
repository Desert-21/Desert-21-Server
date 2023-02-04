package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.LocatedField;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.TrainAction;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.TrainUnitsCostCalculator;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.TrainingMode;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;

import java.util.ArrayList;
import java.util.List;

// Train units
public class MetalActionsGetter implements CategoryActionsGetter<Integer> {

    private final static List<UnitType> ONLY_DROIDS = List.of(
            UnitType.DROID
    );

    private final static List<UnitType> DROIDS_AND_TANKS = List.of(
            UnitType.DROID,
            UnitType.TANK
    );

    private final static List<UnitType> ALL = List.of(
            UnitType.DROID,
            UnitType.TANK,
            UnitType.CANNON
    );

    @Override
    public List<ActionPossibility<Integer>> getActions(Game game, Player player, GameBalanceDto gameBalance) {
        var unlockedTrainingModes = getUnlockedTrainingModes(player);
        return BoardUtils.boardToOwnedLocatedFieldList(game.getFields(), player.getId())
                .stream()
                .filter(field -> field.field().getBuilding().isDefensive())
                .flatMap(field -> getActionPossibilitiesOfField(field, player, unlockedTrainingModes, gameBalance).stream())
                .toList();
    }

    private List<ActionPossibility<Integer>> getActionPossibilitiesOfField(LocatedField locatedField, Player player, List<TrainingMode> unlockedTrainingModes, GameBalanceDto gameBalance) {
        var availableUnitTypes = switch(locatedField.field().getBuilding().getLevel()) {
            case 1 -> ONLY_DROIDS;
            case 2 -> DROIDS_AND_TANKS;
            case 3, 4 -> ALL;
            default -> new ArrayList<UnitType>();
        };
        var playersMetal = player.getResources().getMetal();
        return availableUnitTypes.stream().flatMap(unitType ->
                unlockedTrainingModes.stream().map(mode -> createActionPossibility(locatedField, unitType, mode, gameBalance))
        )
                .filter(possibility -> playersMetal >= possibility.getCost())
                .toList();
    }

    private ActionPossibility<Integer> createActionPossibility(LocatedField locatedField, UnitType unitType, TrainingMode trainingMode, GameBalanceDto gameBalance) {
        var action = new TrainAction(
                locatedField.location(),
                unitType,
                trainingMode
        );
        var cost = TrainUnitsCostCalculator.getTrainingCost(gameBalance, unitType, trainingMode);
        return new ActionPossibility<>(action, cost);
    }


    private List<TrainingMode> getUnlockedTrainingModes(Player player) {
        var modes = new ArrayList<TrainingMode>();
        modes.add(TrainingMode.SMALL_PRODUCTION);
        if (player.ownsUpgrade(LabUpgrade.MEDIUM_PRODUCTION)) {
            modes.add(TrainingMode.MEDIUM_PRODUCTION);
        }
        if (player.ownsUpgrade(LabUpgrade.MASS_PRODUCTION)) {
            modes.add(TrainingMode.MASS_PRODUCTION);
        }
        return modes;
    }
}
