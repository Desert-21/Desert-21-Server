package com.github.maciejmalewicz.Desert21.ai.actionsGetters;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.ActionPossibilityType;
import com.github.maciejmalewicz.Desert21.ai.helpers.BFSFieldWrapper;
import com.github.maciejmalewicz.Desert21.ai.helpers.GameEnhancementWrapper;
import com.github.maciejmalewicz.Desert21.ai.helpers.LocatedArmy;
import com.github.maciejmalewicz.Desert21.ai.helpers.LocatedCannons;
import com.github.maciejmalewicz.Desert21.ai.parameters.AttackActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.BombardActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.FreezeUnitsActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.MoveUnitsActionParameters;
import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// Attack
// Bombard
// Move units
@Service
public class ArmyActionsGetter implements CategoryActionsGetter {

    @Override
    public List<ActionPossibility<?>> getActions(GameEnhancementWrapper game, Player player, GameBalanceDto gameBalance) {
        var boardWrapper = constructBoardWrapper(game);
        BoardUtils.boardToOwnedLocatedFieldList(game.getFields(), player.getId())
                .forEach(locatedField ->
                        performBfsOnLocation(locatedField.location(), boardWrapper, player, gameBalance)
                );

        return extractActionsFromEnrichedBoard(boardWrapper);
    }

    private List<ActionPossibility<?>> extractActionsFromEnrichedBoard(BFSFieldWrapper[][] boardWrapper) {
        var acc = new LinkedList<ActionPossibility<?>>();
        for (BFSFieldWrapper[] row : boardWrapper) {
            for (BFSFieldWrapper field: row) {
                var fromField = Stream.of(
                        field.getAttackActionPossibility(),
                        field.getBombardActionPossibility(),
                        field.getMoveUnitsActionPossibility(),
                        field.getFreezeUnitsActionPossibility()
                )
                        .filter(Objects::nonNull)
                        .toList();
                acc.addAll(fromField);
            }
        }
        return acc;
    }

    private void resetVisitedFlag(BFSFieldWrapper[][] board) {
        for (BFSFieldWrapper[] row : board) {
            for (BFSFieldWrapper field: row) {
                field.setAlreadyVisited(false);
            }
        }
    }

    private BFSFieldWrapper[][] constructBoardWrapper(Game game) {
        var fields = game.getFields();
        var acc = new BFSFieldWrapper[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            acc[i] = new BFSFieldWrapper[fields[i].length];
            for (int j = 0; j < fields[i].length; j++) {
                acc[i][j] = new BFSFieldWrapper(fields[i][j], new Location(i, j));
            }
        }
        return acc;
    }

    private void performBfsOnLocation(Location location, BFSFieldWrapper[][] boardWrapper, Player player, GameBalanceDto gameBalance) {
        var fieldWrapper = boardWrapper[location.row()][location.col()];
        if (fieldWrapper.getField().getArmy().isEmpty()) {
            return;
        } else {
            enrichFieldWithFreezeActionPossibility(fieldWrapper);
        }
        var maxTravellingDistance = getMaxTravellingDistance(fieldWrapper.getField().getArmy(), gameBalance);
        int currentDistance = 1;

        // Changes every loop iteration
        var currentFields = getNeighbouringNonVisitedFields(boardWrapper, location);
        fieldWrapper.setAlreadyVisited(true); // to prevent from movement A -> A actions
        while (currentDistance <= maxTravellingDistance) {
            var nextFields = new ArrayList<BFSFieldWrapper>();

            for (BFSFieldWrapper field : currentFields) {
                field.setAlreadyVisited(true);
                if (field.getField().getOwnerId() == null) {
                    enrichFieldWithAttackActionPossibility(fieldWrapper, field, currentDistance, gameBalance);
                } else if (!player.getId().equals(field.getField().getOwnerId())) {
                    enrichFieldWithAttackActionPossibility(fieldWrapper, field, currentDistance, gameBalance);
                    enrichFieldWithBombardActionPossibility(fieldWrapper, field, currentDistance, gameBalance);
                } else {
                    enrichFieldWithMovementActionPossibility(fieldWrapper, field, currentDistance, gameBalance);
                    var toVisitFromField = getNeighbouringNonVisitedFields(boardWrapper, field.getLocation());
                    nextFields.addAll(toVisitFromField);
                }
            }
            currentFields = nextFields;
            currentDistance++;
        }
        resetVisitedFlag(boardWrapper);
    }

    private List<BFSFieldWrapper> getNeighbouringFields(BFSFieldWrapper[][] board, Location location) {
        var row = location.row();
        var col = location.col();
        var top = row > 0 ? board[row - 1][col] : null;
        var bottom = row < board.length - 1 ? board[row + 1][col] : null;
        var left = col > 0 ? board[row][col - 1] : null;
        var right = col < board[row].length - 1 ? board[row][col + 1] : null;
        return Stream.of(top, bottom, left, right)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<BFSFieldWrapper> getNeighbouringNonVisitedFields(BFSFieldWrapper[][] board, Location location) {
        return getNeighbouringFields(board, location).stream()
                .filter(fieldWrapper -> !fieldWrapper.isAlreadyVisited())
                .toList();
    }

    private int getMaxTravellingDistance(Army army, GameBalanceDto gameBalance) {
        var droidsSpeed = army.getDroids() > 0 ? gameBalance.combat().droids().getFieldsTraveledPerTurn() : 0;
        var tanksSpeed = army.getTanks() > 0 ? gameBalance.combat().tanks().getFieldsTraveledPerTurn() : 0;
        var cannonsSpeed = army.getCannons() > 0 ? gameBalance.combat().cannons().getFieldsTraveledPerTurn() : 0;
        return IntStream.of(droidsSpeed, tanksSpeed, cannonsSpeed).max().orElse(0);
    }

    private Army filterArmyByDistanceTravelled(Army army, int distance, GameBalanceDto gameBalance) {
        var balance = gameBalance.combat();
        var droids = distance > balance.droids().getFieldsTraveledPerTurn() ? 0 : army.getDroids();
        var tanks = distance > balance.tanks().getFieldsTraveledPerTurn() ? 0 : army.getTanks();
        var cannons = distance > balance.cannons().getFieldsTraveledPerTurn() ? 0 : army.getCannons();
        return new Army(droids, tanks, cannons);
    }

    private void enrichFieldWithFreezeActionPossibility(BFSFieldWrapper fieldWrapper) {
        var freezeActionParameters = new FreezeUnitsActionParameters(fieldWrapper.getLocation(), fieldWrapper.getField().getArmy());
        fieldWrapper.setFreezeUnitsActionPossibility(new ActionPossibility<>(ActionPossibilityType.FREEZE_UNITS, freezeActionParameters));
    }

    private void enrichFieldWithAttackActionPossibility(BFSFieldWrapper fromField, BFSFieldWrapper toField, int distanceTravelled, GameBalanceDto gameBalance) {
        var actionPossibility = toField.getAttackActionPossibility();
        var army = filterArmyByDistanceTravelled(fromField.getField().getArmy(), distanceTravelled + 1, gameBalance);
        var locatedArmy = new LocatedArmy(fromField.getLocation(), army);
        if (actionPossibility == null) {
            var newParameters = new AttackActionParameters(toField.getLocation(), locatedArmy);
            toField.setAttackActionPossibility(new ActionPossibility<>(ActionPossibilityType.ATTACK, newParameters));
        } else {
            toField.getAttackActionPossibility().getParameters().getFromArmies().add(locatedArmy);
        }
    }

    private void enrichFieldWithMovementActionPossibility(BFSFieldWrapper fromField, BFSFieldWrapper toField, int distanceTravelled, GameBalanceDto gameBalance) {
        var actionPossibility = toField.getMoveUnitsActionPossibility();
        var army = filterArmyByDistanceTravelled(fromField.getField().getArmy(), distanceTravelled + 1, gameBalance);
        var locatedArmy = new LocatedArmy(fromField.getLocation(), army);
        if (actionPossibility == null) {
            var newParameters = new MoveUnitsActionParameters(toField.getLocation(), locatedArmy);
            toField.setMoveUnitsActionPossibility(new ActionPossibility<>(ActionPossibilityType.MOVE_UNITS, newParameters));
        } else {
            toField.getMoveUnitsActionPossibility().getParameters().getFromArmies().add(locatedArmy);
        }
    }

    private void enrichFieldWithBombardActionPossibility(BFSFieldWrapper fromField, BFSFieldWrapper toField, int distanceTravelled, GameBalanceDto gameBalance) {
        var actionPossibility = toField.getBombardActionPossibility();
        var army = filterArmyByDistanceTravelled(fromField.getField().getArmy(), distanceTravelled + 1, gameBalance);
        var locatedCannons = new LocatedCannons(fromField.getLocation(), army.getCannons());
        if (actionPossibility == null) {
            var newParameters = new BombardActionParameters(toField.getLocation(), locatedCannons);
            toField.setBombardActionPossibility(new ActionPossibility<>(ActionPossibilityType.BOMBARD, newParameters));
        } else {
            toField.getBombardActionPossibility().getParameters().getFromCannons().add(locatedCannons);
        }
    }
}