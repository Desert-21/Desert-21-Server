package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttackingExecutor implements EventExecutor<AttackingEvent> {

    @Override
    public EventExecutionResult execute(List<AttackingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var attacksCombinedByLocation = events.stream()
                .collect(Collectors.groupingBy(AttackingEvent::getLocation))
                .entrySet()
                .stream()
                .map(this::mergeAttacks)
                .toList();

        return null;
    }

    private AttackingEvent mergeAttacks(Map.Entry<Location, List<AttackingEvent>> entry) {
        var location = entry.getKey();
        return entry.getValue().stream()
                .reduce(
                        new AttackingEvent(location, new Army(0, 0, 0)),
                        (prev, next) -> new AttackingEvent(location, prev.getArmy().combineWith(next.getArmy()))
                );
    }
}
