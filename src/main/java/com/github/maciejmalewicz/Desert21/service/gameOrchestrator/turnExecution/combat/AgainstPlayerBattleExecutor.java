package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import org.springframework.stereotype.Service;

@Service
public class AgainstPlayerBattleExecutor {

    private final ArmyPowerCalculator armyPowerCalculator;
    private final WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator;
    private final AttackersArmyAfterAttackCalculator attackersArmyAfterAttackCalculator;
    private final DefendersArmyAfterAttackCalculator defendersArmyAfterAttackCalculator;
    private final ScarabsGenerator scarabsGenerator;

    public AgainstPlayerBattleExecutor(ArmyPowerCalculator armyPowerCalculator, WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator, AttackersArmyAfterAttackCalculator attackersArmyAfterAttackCalculator, DefendersArmyAfterAttackCalculator defendersArmyAfterAttackCalculator, ScarabsGenerator scarabsGenerator) {
        this.armyPowerCalculator = armyPowerCalculator;
        this.winnersArmyDestructionRatioCalculator = winnersArmyDestructionRatioCalculator;
        this.attackersArmyAfterAttackCalculator = attackersArmyAfterAttackCalculator;
        this.defendersArmyAfterAttackCalculator = defendersArmyAfterAttackCalculator;
        this.scarabsGenerator = scarabsGenerator;
    }

    public BattleResult executeBattleAgainstPlayer(Army attackersArmy, TurnExecutionContext context, Field attackedField) throws NotAcceptableException {
        var opponent = context.game().getOtherPlayer()
                .orElseThrow(() -> new NotAcceptableException("Opponent could not be identified!"));

        var attackersArmyBefore = new FightingArmy(attackersArmy.getDroids(), attackersArmy.getTanks(), attackersArmy.getCannons(), 0);
        var defendersArmyBefore = getDefendersFightingArmy(attackedField.getArmy(), opponent, context);

        var attackersPower = armyPowerCalculator.calculateAttackersPower(attackersArmyBefore, context);
        var defendersPower = armyPowerCalculator.calculateDefendersPower(defendersArmyBefore, context, opponent, attackedField);

        var attackerHaveWon = attackersPower > defendersPower;
        var winnersPower = attackerHaveWon ? attackersPower : defendersPower;
        var losersPower = attackerHaveWon ? defendersPower : attackersPower;

        var destructionRatio = winnersArmyDestructionRatioCalculator.calculateDestructionRatio(winnersPower, losersPower, context.gameBalance());

        var attackersArmyAfter = attackersArmyAfterAttackCalculator.calculateAttackersArmyAfter(
                attackersArmyBefore,
                attackerHaveWon,
                destructionRatio,
                context.player(),
                context.gameBalance()
        );
        var defendersArmyAfter = defendersArmyAfterAttackCalculator.calculateDefendersArmyAfter(
                defendersArmyBefore,
                attackerHaveWon,
                destructionRatio
        );
        return new BattleResult(
                attackersArmyBefore,
                defendersArmyBefore,
                attackersArmyAfter,
                defendersArmyAfter,
                attackerHaveWon,
                false
        );
    }

    private FightingArmy getDefendersFightingArmy(Army fieldArmy, Player defender, TurnExecutionContext context) {
        if (!defender.ownsUpgrade(LabUpgrade.KING_OF_DESERT)) {
            return new FightingArmy(fieldArmy.getDroids(), fieldArmy.getTanks(), fieldArmy.getCannons(), 0);
        }
        var scarabDefenders = scarabsGenerator.generateScarabs(context);
        return new FightingArmy(fieldArmy.getDroids(), fieldArmy.getTanks(), fieldArmy.getCannons(), scarabDefenders);
    }
}
