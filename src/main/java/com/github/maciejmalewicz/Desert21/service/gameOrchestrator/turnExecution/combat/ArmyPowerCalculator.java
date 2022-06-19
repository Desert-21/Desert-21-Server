package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
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

    public int calculateDefendersPower(FightingArmy army, TurnExecutionContext context, Player opponent, Field defendedField) throws NotAcceptableException {
        var basePower = getArmyBasePower(army, context.gameBalance().combat());
        var postTowerPower = optionallyApplyTowerPowerBonuses(
                basePower,
                defendedField.getBuilding(),
                context.gameBalance()
        );
        var postFactoryTurretPower = optionallyApplyFactoryTurretBonuses(
                postTowerPower,
                defendedField.getBuilding(),
                context.gameBalance(),
                opponent
        );
        var postImprovedDroidsPower = optionallyApplyImprovedDroidsPower(
                postFactoryTurretPower,
                defendedField.getBuilding(),
                context.gameBalance(),
                opponent
        );
        var postImprovedTanksPower = optionallyApplyImprovedTanksPower(
                postImprovedDroidsPower,
                opponent,
                context.gameBalance().upgrades()
        );
        var totalPreAdvancedTacticsArmyPower = postImprovedTanksPower.droids()
                + postImprovedTanksPower.tanks()
                + postImprovedTanksPower.cannons()
                + postImprovedTanksPower.staticBonus();
        var totalPostAdvancedTacticsPower = optionallyApplyAdvancedTacticsPower(
                totalPreAdvancedTacticsArmyPower,
                opponent,
                context.gameBalance().upgrades()
        );
        return optionallyApplyGeneratedScarabsPower(totalPostAdvancedTacticsPower, army.scarabs(), context);

    }

    public int calculateAttackersPower(FightingArmy army, TurnExecutionContext context) {
        var basePower = getArmyBasePower(army, context.gameBalance().combat());
        var postImprovedTanksPower = optionallyApplyImprovedTanksPower(
                basePower,
                context.player(),
                context.gameBalance().upgrades()
        );
        var totalPreAdvancedTacticsArmyPower = postImprovedTanksPower.droids()
                + postImprovedTanksPower.tanks()
                + postImprovedTanksPower.cannons()
                + postImprovedTanksPower.staticBonus();
        return optionallyApplyAdvancedTacticsPower(
                totalPreAdvancedTacticsArmyPower,
                context.player(),
                context.gameBalance().upgrades()
        );
    }

    private ArmyPower getArmyBasePower(FightingArmy army, AllCombatBalanceDto balance) {
        return new ArmyPower(
                army.droids() * balance.droids().getPower(),
                army.tanks() * balance.tanks().getPower(),
                army.cannons() * balance.cannons().getPower(),
                0
        );
    }

    private ArmyPower optionallyApplyTowerPowerBonuses(ArmyPower armyPower, Building building, GameBalanceDto balance) throws NotAcceptableException {
        if (!building.isDefensive()) {
            return armyPower;
        }
        var level = building.getLevel();
        var config = (TowerConfig) BuildingUtils.buildingTypeToConfig(building.getType(), balance);
        var baseProtection = config.getBaseProtection().getAtLevel(level);
        var unitBonus = config.getUnitBonus().getAtLevel(level);

        return new ArmyPower(
                armyPower.droids() + (int) Math.round(armyPower.droids() * unitBonus),
                armyPower.tanks() + (int) Math.round(armyPower.tanks() * unitBonus),
                armyPower.cannons() + (int) Math.round(armyPower.cannons() * unitBonus),
                armyPower.staticBonus() + baseProtection
        );
    }

    private ArmyPower optionallyApplyFactoryTurretBonuses(ArmyPower armyPower, Building building, GameBalanceDto balance, Player player) {
        if (!building.isFactory()) {
            return armyPower;
        }
        if (!player.ownsUpgrade(LabUpgrade.FACTORY_TURRET)) {
            return armyPower;
        }
        var towerConfig = balance.buildings().tower();
        var towerLevel = balance.upgrades().control().getControlBranchConfig().getFactoryTurretTowerLevel();
        var baseProtection = towerConfig.getBaseProtection().getAtLevel(towerLevel);
        var unitBonus = towerConfig.getUnitBonus().getAtLevel(towerLevel);

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
        var config = balance.upgrades().combat().getCombatBranchConfig();
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
        var tanksPowerBonus = balanceDto.combat().getCombatBranchConfig().getImprovedTanksPowerBonus();
        var tanksPowerRatio = 1 + tanksPowerBonus;
        return new ArmyPower(
                armyPower.droids(),
                (int) (armyPower.tanks() * tanksPowerRatio),
                armyPower.cannons(),
                armyPower.staticBonus()
        );
    }

    private int optionallyApplyAdvancedTacticsPower(int power, Player player, AllUpgradesBalanceDto balanceDto) {
        if (!player.ownsUpgrade(LabUpgrade.ADVANCED_TACTICS)) {
            return power;
        }
        var combatConfig = balanceDto.combat().getCombatBranchConfig();
        var powerPerStep = combatConfig.getAdvancedTacticsPowerBonusPerReferencePower();
        var step = (double) combatConfig.getAdvancedTacticsReferencePower();
        var amountOfSteps = Math.floor(power / step);
        var totalBonusRatio = amountOfSteps * powerPerStep;
        return (int) (power + Math.round(power * totalBonusRatio));
    }

    private int optionallyApplyGeneratedScarabsPower(int power, int scarabsAmount, TurnExecutionContext context) {
        if (scarabsAmount <= 0) {
            return power;
        }
        var additionalScarabsPower = scarabsPowerCalculator.calculateScarabsPower(scarabsAmount, context);
        return power + additionalScarabsPower;
    }
}
