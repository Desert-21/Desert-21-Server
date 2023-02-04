package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.RocketStrikeDestroysRocketLauncherEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.RocketStrikeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.RocketStrikeEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RocketStrikeExecutor implements EventExecutor<RocketStrikeEvent> {

    @Override
    public EventExecutionResult execute(List<RocketStrikeEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        if (events.isEmpty()) {
            return new EventExecutionResult(context, new ArrayList<>());
        }

        var isRocketDiscounted = context.player().isNextRocketFree();
        if (!isRocketDiscounted) {
            var doneRocketStrikes = context.player().getRocketStrikesDone();
            context.player().setRocketStrikesDone(doneRocketStrikes + 1);
        } else {
            context.player().setNextRocketFree(false);
        }

        var event = events.get(0);
        var field = BoardUtils.fieldAtLocation(context.game().getFields(), event.getLocation());
        var eventResults = new ArrayList<EventResult>();
        if (event.isAttackingRocket()) {
            field.setBuilding(new Building(BuildingType.EMPTY_FIELD));
            eventResults.add(new RocketStrikeDestroysRocketLauncherEventResult(event.getLocation()));
        } else {
            var defendersBefore = field.getArmy();
            var defendersAfter = damageArmy(defendersBefore, context);
            field.setArmy(defendersAfter);
            eventResults.add(new RocketStrikeEventResult(event.getLocation(), defendersBefore, defendersAfter));
        }
        return new EventExecutionResult(context, eventResults);
    }

    private Army damageArmy(Army defenders, TurnExecutionContext context) {
        if (defenders == null) {
            return null;
        }
        var damage = context.gameBalance().general().getRocketStrikeDamage();
        var remainingUnitsRatio = 1 - damage;

        return new Army(
                (int) (defenders.getDroids() * remainingUnitsRatio),
                (int) (defenders.getTanks() * remainingUnitsRatio),
                (int) (defenders.getCannons() * remainingUnitsRatio)
        );
    }
}
