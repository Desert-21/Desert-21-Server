package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BombardingBattleExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BombardingFailedEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BombardingSucceededEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BombardingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.fieldAtLocation;

@Service
public class BombardingExecutor implements EventExecutor<BombardingEvent> {

    private final BombardingBattleExecutor bombardingBattleExecutor;

    public BombardingExecutor(BombardingBattleExecutor bombardingBattleExecutor) {
        this.bombardingBattleExecutor = bombardingBattleExecutor;
    }

    @Override
    public EventExecutionResult execute(List<BombardingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var bombardingResults = events.stream()
                .collect(Collectors.groupingBy(BombardingEvent::getTarget))
                .entrySet()
                .stream()
                .map(this::mergeBombarding)
                .map(e -> executeBombarding(e, context))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        var eventResults = new ArrayList<EventResult>();
        for (Pair<Location, BattleResult> pair: bombardingResults) {
            var location = pair.getFirst();
            var field = fieldAtLocation(context.game().getFields(), location);
            var battleResult = pair.getSecond();
            var defendersAfter = battleResult.defendersAfter();
            var newArmy = new Army(defendersAfter.droids(), defendersAfter.tanks(), defendersAfter.cannons());
            field.setArmy(newArmy);

            var eventResult = battleToEventResult(location, battleResult);
            eventResults.add(eventResult);
        }
        return new EventExecutionResult(context, eventResults);
    }

    private EventResult battleToEventResult(Location location, BattleResult battleResult) {
        var attackerHasWon = battleResult.haveAttackersWon();
        if (attackerHasWon) {
            return new BombardingSucceededEventResult(location, battleResult);
        } else {
            return new BombardingFailedEventResult(location, battleResult);
        }
    }

    private Optional<Pair<Location, BattleResult>> executeBombarding(BombardingEvent event, TurnExecutionContext context) {
        try {
           var battleResult = bombardingBattleExecutor.executeBombarding(event, context);
           var entry = Pair.of(event.getTarget(), battleResult);
           return Optional.of(entry);
        } catch (NotAcceptableException e) {
            return Optional.empty();
        }
    }

    private BombardingEvent mergeBombarding(Map.Entry<Location, List<BombardingEvent>> entry) {
        var totalCannons = entry.getValue().stream()
                .map(BombardingEvent::getCannons)
                .reduce(Integer::sum)
                .orElse(0);
        return new BombardingEvent(entry.getKey(), totalCannons);
    }
}
