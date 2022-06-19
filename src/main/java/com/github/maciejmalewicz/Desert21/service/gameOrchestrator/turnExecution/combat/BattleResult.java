package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

public record BattleResult(
        FightingArmy attackersBefore,
        FightingArmy defendersBefore,
        FightingArmy attackersAfter,
        FightingArmy defendersAfter,
        boolean haveAttackersWon,
        boolean wasUnoccupied
) {
}
