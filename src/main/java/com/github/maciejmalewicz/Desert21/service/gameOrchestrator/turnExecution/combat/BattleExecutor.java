package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.AttackingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

@Service
public class BattleExecutor {

    private final AgainstScarabsBattleExecutor againstScarabsBattleExecutor;
    private final AgainstPlayerBattleExecutor againstPlayerBattleExecutor;

    public BattleExecutor(AgainstScarabsBattleExecutor againstScarabsBattleExecutor, AgainstPlayerBattleExecutor againstPlayerBattleExecutor) {
        this.againstScarabsBattleExecutor = againstScarabsBattleExecutor;
        this.againstPlayerBattleExecutor = againstPlayerBattleExecutor;
    }

    public BattleResult executeBattle(AttackingEvent attack, TurnExecutionContext context) throws NotAcceptableException {
        var attackedField = BoardUtils.fieldAtLocation(context.game().getFields(), attack.getLocation());
        if (attackedField.getOwnerId() == null) {
            return againstScarabsBattleExecutor.executeBattleAgainstScarabs(attack.getArmy(), context);
        } else {
            return againstPlayerBattleExecutor.executeBattleAgainstPlayer(attack.getArmy(), context, attackedField);
        }
    }
}
