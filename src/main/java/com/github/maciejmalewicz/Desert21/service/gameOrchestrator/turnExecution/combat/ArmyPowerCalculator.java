package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.balance.AllCombatBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.AllUpgradesBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.TowerConfig;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;
import org.springframework.stereotype.Service;

@Service
public class ArmyPowerCalculator {

    private final ScarabsPowerCalculator scarabsPowerCalculator;

    public ArmyPowerCalculator(ScarabsPowerCalculator scarabsPowerCalculator) {
        this.scarabsPowerCalculator = scarabsPowerCalculator;
    }

    public int calculateDefendersPower(FightingArmy army, TurnExecutionContext context, Player defender, Player attacker, Field defendedField, Army attackers) throws NotAcceptableException {
        var basePower = getArmyBasePower(army, context.gameBalance().combat());
        var postTowerPower = optionallyApplyTowerPowerBonuses(
                basePower,
                defendedField.getBuilding(),
                context.gameBalance(),
                attacker,
                attackers
        );
        var postFactoryTurretPower = optionallyApplyFactoryTurretBonuses(
                postTowerPower,
                defendedField.getBuilding(),
                context.gameBalance(),
                defender,
                attacker,
                attackers
        );
        var postImprovedDroidsPower = optionallyApplyImprovedDroidsPower(
                postFactoryTurretPower,
                defendedField.getBuilding(),
                context.gameBalance(),
                defender
        );
        var postImprovedTanksPower = optionallyApplyImprovedTanksPower(
                postImprovedDroidsPower,
                defender,
                context.gameBalance().upgrades()
        );
        var totalBasicArmyPower = postImprovedTanksPower.droids()
                + postImprovedTanksPower.tanks()
                + postImprovedTanksPower.cannons()
                + postImprovedTanksPower.staticBonus();
        return Math.round(optionallyApplyGeneratedScarabsPower(totalBasicArmyPower, army.scarabs(), context));

    }

    public int calculateAttackersPower(FightingArmy army, TurnExecutionContext context) {
        var basePower = getArmyBasePower(army, context.gameBalance().combat());
        var postImprovedTanksPower = optionallyApplyImprovedTanksPower(
                basePower,
                context.player(),
                context.gameBalance().upgrades()
        );
        return Math.round(postImprovedTanksPower.droids()
                + postImprovedTanksPower.tanks()
                + postImprovedTanksPower.cannons()
                + postImprovedTanksPower.staticBonus());
    }

    private ArmyPower getArmyBasePower(FightingArmy army, AllCombatBalanceDto balance) {
        return new ArmyPower(
                army.droids() * balance.droids().getPower(),
                army.tanks() * balance.tanks().getPower(),
                army.cannons() * balance.cannons().getPower(),
                0
        );
    }

    private ArmyPower optionallyApplyTowerPowerBonuses(ArmyPower armyPower, Building building, GameBalanceDto balance, Player attacker, Army attackingArmy) throws NotAcceptableException {
        if (!building.isDefensive()) {
            return armyPower;
        }
        var level = building.getLevel();
        var config = (TowerConfig) BuildingUtils.buildingTypeToConfig(building.getType(), balance);
        var baseProtection = config.getBaseProtection().getAtLevel(level);
        var unitBonus = config.getUnitBonus().getAtLevel(level);

        // Advanced tactics
        if (isAdvancedTacticsBonusApplicable(attacker, attackingArmy)) {
            var defencePenalty = balance.upgrades().combat().getBalanceConfig().getAdvancedTacticsTowerBonusesDecrease();
            baseProtection = (int) Math.round(baseProtection - (baseProtection * defencePenalty));
            unitBonus = unitBonus - (unitBonus * defencePenalty);
        }

        return new ArmyPower(
                armyPower.droids() + (int) Math.round(armyPower.droids() * unitBonus),
                armyPower.tanks() + (int) Math.round(armyPower.tanks() * unitBonus),
                armyPower.cannons() + (int) Math.round(armyPower.cannons() * unitBonus),
                armyPower.staticBonus() + baseProtection
        );
    }

    private ArmyPower optionallyApplyFactoryTurretBonuses(ArmyPower armyPower, Building building, GameBalanceDto balance, Player defender, Player attacker, Army attackingArmy) {
        if (!building.isFactory()) {
            return armyPower;
        }
        if (!defender.ownsUpgrade(LabUpgrade.FACTORY_TURRET)) {
            return armyPower;
        }
        var towerConfig = balance.buildings().tower();
        var towerLevel = balance.upgrades().control().getBalanceConfig().getFactoryTurretTowerLevel();
        var baseProtection = towerConfig.getBaseProtection().getAtLevel(towerLevel);
        var unitBonus = towerConfig.getUnitBonus().getAtLevel(towerLevel);

        // Advanced tactics
        if (isAdvancedTacticsBonusApplicable(attacker, attackingArmy)) {
            var defencePenalty = balance.upgrades().combat().getBalanceConfig().getAdvancedTacticsTowerBonusesDecrease();
            baseProtection = (int) Math.round(baseProtection - (baseProtection * defencePenalty));
            unitBonus = unitBonus - (unitBonus * defencePenalty);
        }

        return new ArmyPower(
                armyPower.droids() + (int) Math.round(armyPower.droids() * unitBonus),
                armyPower.tanks() + (int) Math.round(armyPower.tanks() * unitBonus),
                armyPower.cannons() + (int) Math.round(armyPower.cannons() * unitBonus),
                armyPower.staticBonus() + baseProtection
        );
    }

    private ArmyPower optionallyApplyImprovedDroidsPower(ArmyPower armyPower, Building building, GameBalanceDto balance, Player player) {
        if (!player.ownsUpgrade(LabUpgrade.IMPROVED_DROIDS)) {
            return armyPower;
        }
        var config = balance.upgrades().combat().getBalanceConfig();
        var bonus = building.isDefensive() ?
                config.getImprovedDroidsBaseAtTowerDefenceBonus() :
                config.getImprovedDroidsBaseDefenceBonus();
        return new ArmyPower(
                armyPower.droids() + (int) Math.round(armyPower.droids() * bonus),
                armyPower.tanks(),
                armyPower.cannons(),
                armyPower.staticBonus()
        );
    }

    private ArmyPower optionallyApplyImprovedTanksPower(ArmyPower armyPower, Player player, AllUpgradesBalanceDto balanceDto) {
        if (!player.ownsUpgrade(LabUpgrade.IMPROVED_TANKS)) {
            return armyPower;
        }
        var tanksPowerBonus = balanceDto.combat().getBalanceConfig().getImprovedTanksPowerBonus();
        var tanksPowerRatio = 1 + tanksPowerBonus;
        return new ArmyPower(
                armyPower.droids(),
                (int) (armyPower.tanks() * tanksPowerRatio),
                armyPower.cannons(),
                armyPower.staticBonus()
        );
    }

    private int optionallyApplyGeneratedScarabsPower(int power, int scarabsAmount, TurnExecutionContext context) {
        if (scarabsAmount <= 0) {
            return power;
        }
        var additionalScarabsPower = scarabsPowerCalculator.calculateScarabsPower(scarabsAmount, context);
        return power + additionalScarabsPower;
    }

    private boolean isAdvancedTacticsBonusApplicable(Player attacker, Army attackingArmy) {
        return attacker.ownsUpgrade(LabUpgrade.ADVANCED_TACTICS)
                && attackingArmy.getDroids() > 0
                && attackingArmy.getTanks() > 0
                && attackingArmy.getCannons() > 0;
    }
}
