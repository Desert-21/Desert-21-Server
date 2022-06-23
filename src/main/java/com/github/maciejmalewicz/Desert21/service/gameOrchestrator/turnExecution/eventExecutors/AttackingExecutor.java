package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleExecutor;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.components.FieldConquestService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttackingExecutor implements EventExecutor<AttackingEvent> {

    private final BattleExecutor battleExecutor;
    private final FieldConquestService fieldConquestService;

    public AttackingExecutor(BattleExecutor battleExecutor, FieldConquestService fieldConquestService) {
        this.battleExecutor = battleExecutor;
        this.fieldConquestService = fieldConquestService;
    }

    @Override
    public EventExecutionResult execute(List<AttackingEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var battleResultsCombinedByLocationOpts = events.stream()
                .collect(Collectors.groupingBy(AttackingEvent::getLocation))
                .entrySet()
                .stream()
                .map(this::mergeAttacks)
                .map(attack -> executeBattle(attack, context))
                .toList();
        if (battleResultsCombinedByLocationOpts.stream().anyMatch(Optional::isEmpty)) {
            throw new NotAcceptableException("Attack has failed!");
        }
        var locationToBattleResults = battleResultsCombinedByLocationOpts.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        var updatedContext = context;
        for (Pair<Location, BattleResult> locationBattleResultPair: locationToBattleResults) {
            updatedContext = fieldConquestService.executeOptionalFieldConquest(locationBattleResultPair, updatedContext);
        }

        var eventResults = locationToBattleResults.stream()
                .map(this::locationAndBattleResultToEventResult)
                .toList();
        return new EventExecutionResult(updatedContext, eventResults);
    }

    private EventResult locationAndBattleResultToEventResult(Pair<Location, BattleResult> locationAndBattleResultPair) {
        var location = locationAndBattleResultPair.getFirst();
        var battleResult = locationAndBattleResultPair.getSecond();
        var wasUnoccupied = battleResult.wasUnoccupied();
        var hasAttackerWon = battleResult.haveAttackersWon();

        if (wasUnoccupied && hasAttackerWon) {
            return new UnoccupiedFieldConquestSucceeded(location, battleResult);
        } else if (wasUnoccupied) {
            return new UnoccupiedFieldConquestFailed(location, battleResult);
        } else if (hasAttackerWon) {
            return new EnemyFieldConquestSucceeded(location, battleResult);
        } else {
            return new EnemyFieldConquestFailed(location, battleResult);
        }
    }

    private Optional<Pair<Location, BattleResult>> executeBattle(AttackingEvent event, TurnExecutionContext context) {
        try {
            var battleResult = battleExecutor.executeBattle(event, context);
            var locationAndBattleResult = Pair.of(event.getLocation(), battleResult);
            return Optional.of(locationAndBattleResult);
        } catch (NotAcceptableException e) {
            return Optional.empty();
        }
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
