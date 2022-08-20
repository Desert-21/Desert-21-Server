package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.LocatableEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.LocationUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class FieldConquestService {

    public TurnExecutionContext executeOptionalFieldConquest(
            Pair<Location, BattleResult> locationAndBattleResultPair,
            TurnExecutionContext context) {

        var location = locationAndBattleResultPair.getFirst();
        var battleResult = locationAndBattleResultPair.getSecond();
        var shouldConquer = battleResult.haveAttackersWon();
        try {
            var field = BoardUtils.fieldAtLocation(context.game().getFields(), location);
            if (!shouldConquer) {
                var defendersAfter = battleResult.defendersAfter();
                // handles both player and scarab cases
                field.setArmy(new Army(defendersAfter.droids(), defendersAfter.tanks(), defendersAfter.cannons()));
                return context;
            }

            var attackersAfter = battleResult.attackersAfter();
            clearInvalidEvents(context, location);
            field.setArmy(new Army(attackersAfter.droids(), attackersAfter.tanks(), attackersAfter.cannons()));
            var playersId = context.player().getId();
            field.setOwnerId(playersId);
            return context;
        } catch (NotAcceptableException e) {
            return context;
        }
    }

    private void clearInvalidEvents(TurnExecutionContext context, Location location) {
        var updatedQueue = context.game().getEventQueue().stream()
                .filter(e -> validateEventByConqueredFieldLocation(e, location))
                .toList();
        context.game().setEventQueue(updatedQueue);
    }

    private boolean validateEventByConqueredFieldLocation(GameEvent event, Location location) {
      if (event.getClass() != ArmyTrainingEvent.class && event.getClass() != BuildBuildingEvent.class) {
          return true;
      }
      var locatableEvent = (LocatableEvent) event;
      return !locatableEvent.getLocation().equals(location);
    }
}
