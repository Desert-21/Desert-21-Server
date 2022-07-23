package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BombardingEvent;
import org.springframework.stereotype.Service;

import static com.github.maciejmalewicz.Desert21.utils.BoardUtils.fieldAtLocation;

@Service
public class BombardingBattleExecutor {

    private final ArmyPowerCalculator armyPowerCalculator;
    private final BombardingAttackersPowerCalculator bombardingAttackersPowerCalculator;
    private final WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator;
    private final DefendersArmyAfterAttackCalculator defendersArmyAfterAttackCalculator;

    public BombardingBattleExecutor(ArmyPowerCalculator armyPowerCalculator, ScarabsGenerator scarabsGenerator, BombardingAttackersPowerCalculator bombardingAttackersPowerCalculator, WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator, DefendersArmyAfterAttackCalculator defendersArmyAfterAttackCalculator) {
        this.armyPowerCalculator = armyPowerCalculator;
        this.bombardingAttackersPowerCalculator = bombardingAttackersPowerCalculator;
        this.winnersArmyDestructionRatioCalculator = winnersArmyDestructionRatioCalculator;
        this.defendersArmyAfterAttackCalculator = defendersArmyAfterAttackCalculator;
    }

    public BattleResult executeBombarding(BombardingEvent event, TurnExecutionContext context) throws NotAcceptableException {
        var field = fieldAtLocation(context.game().getFields(), event.getTarget());
        var opponent = context.game().getOtherPlayer()
                .orElseThrow(() -> new NotAcceptableException("Player not found!"));
        var fieldArmy = field.getArmy();
        var defendersArmyBefore = getDefendersFightingArmy(fieldArmy);
        var defendersPower = armyPowerCalculator.calculateDefendersPower(defendersArmyBefore, context, opponent, field);
        var attackersPower = bombardingAttackersPowerCalculator.calculateAttackersPower(event.getCannons(), context);

        var attackerHasWon = attackersPower > defendersPower;
        var winnersPower = attackerHasWon ? attackersPower : defendersPower;
        var losersPower = attackerHasWon ? defendersPower : attackersPower;

        var destructionRatio = winnersArmyDestructionRatioCalculator
                .calculateDestructionRatio(winnersPower, losersPower, context.gameBalance());
        var defendersArmyAfter = defendersArmyAfterAttackCalculator
                .calculateDefendersArmyAfter(defendersArmyBefore, attackerHasWon, destructionRatio);

        var attackersBeforeAndAfter = new FightingArmy(0, 0, event.getCannons(), 0);

        return new BattleResult(
                attackersBeforeAndAfter,
                defendersArmyBefore,
                attackersBeforeAndAfter,
                defendersArmyAfter,
                attackerHasWon,
                false
        );
    }

    private FightingArmy getDefendersFightingArmy(Army fieldArmy) {
        return new FightingArmy(fieldArmy.getDroids(), fieldArmy.getTanks(), fieldArmy.getCannons(), 0);
    }

}
