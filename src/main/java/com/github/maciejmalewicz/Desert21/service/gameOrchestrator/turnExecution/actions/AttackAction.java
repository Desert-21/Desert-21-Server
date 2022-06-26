package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyLeavingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
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
public class AttackAction implements Action {
    private Location from;
    private Location to;
    private List<Location> path;
    private Army army;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        if (path.size() < 2) {
            throw new NotAcceptableException("Path is too short!");
        }
        // units fast enough?
        var pathLengthValidatable = new PathLengthValidatable(path, army);

        // if path makes sense
        var pathConvergenceValidatable = new PathFromAndToConvergenceValidatable(
                path,
                from,
                to
        );
        var pathContinuityValidatable = new PathContinuityValidatable(path);
        var locationBoundsValidatables = path.stream()
                .map(LocationBoundsValidatable::new)
                .toList();

        // path ownership
        var pathUntilTarget = path.subList(0, path.size() - 1);
        var fieldOwnershipValidatables = pathUntilTarget.stream()
                .map(l -> new FieldOwnershipValidatable(getFieldAtLocationChecked(context, l), context.player()))
                .toList();
        var fieldNonOwnershipValidatable = new FieldNonOwnershipValidatable(
                BoardUtils.fieldAtLocation(context.game().getFields(), to),
                context.player()
        );

        var enoughUnitsValidatable = new EnoughUnitsValidatable(army, from);

        var allSingleUnitsValidatables = List.of(
                pathLengthValidatable,
                pathConvergenceValidatable,
                pathContinuityValidatable,
                fieldNonOwnershipValidatable,
                enoughUnitsValidatable
        );

        return Stream.of(
                        allSingleUnitsValidatables,
                        locationBoundsValidatables,
                        fieldOwnershipValidatables
                )
                .flatMap(Collection::stream)
                .map(ActionValidatable.class::cast)
                .toList();
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var armyLeavingEvent = new ArmyLeavingEvent(from, army);
        var attackingEvent = new AttackingEvent(to, army);
        return List.of(armyLeavingEvent, attackingEvent);
    }

    private Field getFieldAtLocationChecked(TurnExecutionContext context, Location location) {
        try {
            return BoardUtils.fieldAtLocation(context.game().getFields(), location);
        } catch (NotAcceptableException e) {
            return null;
        }
    }
}
