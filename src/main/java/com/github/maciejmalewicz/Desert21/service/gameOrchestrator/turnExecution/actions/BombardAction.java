package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BombardingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BombardAction implements Action {
    @NonNull
    private Location from;
    @NonNull
    private Location to;
    @NonNull
    private List<Location> path;
    @NonNull
    private Integer cannonsAmount;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        if (path.size() < 2) {
            throw new NotAcceptableException("Path is too short!");
        }
        var army = new Army(0, 0, cannonsAmount);

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
        var isFieldEnemyValidatable = new IsFieldEnemyValidatable(
                BoardUtils.fieldAtLocation(context.game().getFields(), to),
                context.player()
        );

        var enoughUnitsValidatable = new EnoughUnitsValidatable(army, from);

        var isBombardingUnlockedValidatable = new IsBombardingUnlockedValidatable();

        var allSingleUnitsValidatables = List.of(
                pathLengthValidatable,
                pathConvergenceValidatable,
                pathContinuityValidatable,
                isFieldEnemyValidatable,
                enoughUnitsValidatable,
                isBombardingUnlockedValidatable
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
        return List.of(
                new BombardingEvent(to, cannonsAmount)
        );
    }

    private Field getFieldAtLocationChecked(TurnExecutionContext context, Location location) {
        try {
            return BoardUtils.fieldAtLocation(context.game().getFields(), location);
        } catch (NotAcceptableException e) {
            return null;
        }
    }
}
