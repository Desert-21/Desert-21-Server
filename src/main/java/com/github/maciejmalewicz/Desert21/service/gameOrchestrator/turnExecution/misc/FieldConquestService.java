package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
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
            field.setArmy(new Army(attackersAfter.droids(), attackersAfter.tanks(), attackersAfter.cannons()));
            var playersId = context.player().getId();
            field.setOwnerId(playersId);
            return context;
        } catch (NotAcceptableException e) {
            return context;
        }
    }
}
