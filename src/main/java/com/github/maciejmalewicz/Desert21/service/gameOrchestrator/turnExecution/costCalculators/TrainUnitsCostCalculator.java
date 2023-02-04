package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators;

import com.github.maciejmalewicz.Desert21.dto.balance.AllCombatBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.TrainingMode;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;;

public class TrainUnitsCostCalculator {

    public static int getTrainingCost(GameBalanceDto gameBalance, UnitType unitType, TrainingMode trainingMode) {
        var combatBalance = gameBalance.combat();
        var config = getCombatUnitConfig(combatBalance, unitType);
        var producedUnits = getProducedUnitsAmount(config, trainingMode);
        return config.getCost() * producedUnits;
    }

    private static int getProducedUnitsAmount(CombatUnitConfig config, TrainingMode trainingMode) {
        return switch (trainingMode) {
            case SMALL_PRODUCTION -> config.getSmallProduction();
            case MEDIUM_PRODUCTION -> config.getMediumProduction();
            case MASS_PRODUCTION -> config.getMassProduction();
        };
    }

    private static CombatUnitConfig getCombatUnitConfig(AllCombatBalanceDto combatBalance, UnitType unitType) {
        return switch (unitType) {
            case DROID -> combatBalance.droids();
            case TANK -> combatBalance.tanks();
            case CANNON -> combatBalance.cannons();
        };
    }
}
