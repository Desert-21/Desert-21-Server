package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyEnteringEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyLeavingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveUnitsAction implements Action {

    private Location from;
    private Location to;
    private List<Location> path;
    private Army army;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        var pathConvergenceValidatable = new PathFromAndToConvergenceValidatable(
                path,
                from,
                to
        );
        var pathContinuityValidatable = new PathContinuityValidatable(path);
        var pathLengthValidatable = new PathLengthValidatable(path, army);
        var locationBoundsValidatables = path.stream()
                .map(LocationBoundsValidatable::new)
                .toList();
        var fieldOwnershipValidatables = path.stream()
                .map(l -> new FieldOwnershipValidatable(getFieldAtLocationChecked(context, l), context.player()))
                .toList();

        var enoughUnitsValidatable = new EnoughUnitsValidatable(army, from);
        var allSingleValueValidatables = List.of(
                pathConvergenceValidatable,
                pathContinuityValidatable,
                pathLengthValidatable,
                enoughUnitsValidatable
        );
        return Stream.of(
                locationBoundsValidatables,
                allSingleValueValidatables,
                fieldOwnershipValidatables
        )
                .flatMap(Collection::stream)
                .map(ActionValidatable.class::cast)
                .toList();
    }

    private Field getFieldAtLocationChecked(TurnExecutionContext context, Location location) {
        try {
            return BoardUtils.fieldAtLocation(context.game().getFields(), location);
        } catch (NotAcceptableException e) {
            return null;
        }
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        return List.of(
                new ArmyLeavingEvent(from, army),
                new ArmyEnteringEvent(to, army)
        );
    }
}
