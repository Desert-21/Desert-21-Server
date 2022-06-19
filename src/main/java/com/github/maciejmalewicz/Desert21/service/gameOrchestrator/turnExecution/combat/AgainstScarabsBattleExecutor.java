package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import org.springframework.stereotype.Service;

@Service
public class AgainstScarabsBattleExecutor {

    private final ScarabsGenerator scarabsGenerator;
    private final ScarabsPowerCalculator scarabsPowerCalculator;
    private final ArmyPowerCalculator armyPowerCalculator;
    private final WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator;
    private final AttackersArmyAfterAttackCalculator attackersArmyAfterAttackCalculator;

    public AgainstScarabsBattleExecutor(ScarabsGenerator scarabsGenerator, ScarabsPowerCalculator scarabsPowerCalculator, ArmyPowerCalculator armyPowerCalculator, WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator, AttackersArmyAfterAttackCalculator attackersArmyAfterAttackCalculator) {
        this.scarabsGenerator = scarabsGenerator;
        this.scarabsPowerCalculator = scarabsPowerCalculator;
        this.armyPowerCalculator = armyPowerCalculator;
        this.winnersArmyDestructionRatioCalculator = winnersArmyDestructionRatioCalculator;
        this.attackersArmyAfterAttackCalculator = attackersArmyAfterAttackCalculator;
    }

    public BattleResult executeBattleAgainstScarabs(Army attackers, TurnExecutionContext context) {
        var scarabs = scarabsGenerator.generateScarabs(context);

        var attackersArmyBefore = new FightingArmy(attackers.getDroids(), attackers.getTanks(), attackers.getCannons(), 0);
        var defendersArmyBefore = new FightingArmy(0, 0, 0, scarabs);

        var scarabsPower = scarabsPowerCalculator.calculateScarabsPower(scarabs, context);
        var armyPower = armyPowerCalculator.calculateAttackersPower(attackersArmyBefore, context);

        var attackerHaveWon = armyPower > scarabsPower;
        var winnersPower = attackerHaveWon ? armyPower: scarabsPower;
        var losersPower = attackerHaveWon ? scarabsPower : armyPower;

        var destructionRatio = winnersArmyDestructionRatioCalculator.calculateDestructionRatio(winnersPower, losersPower, context.gameBalance());
        var attackersArmyAfter = attackersArmyAfterAttackCalculator.calculateAttackersArmyAfter(
                attackersArmyBefore,
                attackerHaveWon,
                destructionRatio,
                context.player(),
                context.gameBalance()
        );
        var defendersArmyAfter = getDefendersArmyAfter(defendersArmyBefore, attackerHaveWon, destructionRatio);
        return new BattleResult(
                attackersArmyBefore,
                defendersArmyBefore,
                attackersArmyAfter,
                defendersArmyAfter,
                attackerHaveWon,
                true
        );
    }

    private FightingArmy getDefendersArmyAfter(FightingArmy defendersBefore, boolean attackerHasWon, double destructionRatio) {
        if (attackerHasWon) {
            return new FightingArmy(0, 0, 0, 0);
        }
        double remainingUnitsRatio = 1 - destructionRatio;
        var scarabs = (int) Math.floor(defendersBefore.scarabs() * remainingUnitsRatio);
        return new FightingArmy(0, 0, 0, scarabs);
    }
}
