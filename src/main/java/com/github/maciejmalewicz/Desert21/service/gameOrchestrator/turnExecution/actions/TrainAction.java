package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.dto.balance.AllCombatBalanceDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.TrainingMode;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainAction implements Action {

    private Location location;
    private UnitType unitType;
    private TrainingMode trainingMode;

    @Override
    public List<ActionValidatable> getActionValidatables(TurnExecutionContext context) throws NotAcceptableException {
        if (unitType == null || trainingMode == null) {
            throw new NotAcceptableException("Invalid units training data!");
        }
        var field = BoardUtils.fieldAtLocation(context.game().getFields(), location);

        var fieldOwnershipValidatable = new FieldOwnershipValidatable(
                field,
                context.player()
        );
        var trainingCost = getTrainingCost(context);
        var costValidatable = new CostValidatable(trainingCost);
        var singlePerLocationValidatable = new SingleTrainingPerLocationValidatable(location);
        var noPendingTrainingsValidatable = new NoPendingTrainingsValidatable(location);
        var buildingTypeAndLevelValidatable = new BuildingSufficientForUnitsTrainingValidatable(
                field.getBuilding(),
                unitType
        );
        var productionTypeUpgradesValidatable = new ProductionTypeUpgradesOwnershipValidatable(trainingMode);

        return List.of(
                fieldOwnershipValidatable,
                costValidatable,
                singlePerLocationValidatable,
                noPendingTrainingsValidatable,
                buildingTypeAndLevelValidatable,
                productionTypeUpgradesValidatable
        );
    }

    @Override
    public List<GameEvent> getEventExecutables(TurnExecutionContext context) throws NotAcceptableException {
        var combatBalance = context.gameBalance().combat();
        var config = getCombatUnitConfig(combatBalance);

        var actualTurnsToTrain = (config.getTurnsToTrain() - 1) * 2;
        var producedUnits = getProducedUnitsAmount(config);

        return List.of(
                new PaymentEvent(getTrainingCost(context)),
                new ArmyTrainingEvent(actualTurnsToTrain, location, unitType, producedUnits)
        );
    }


    private ResourceSet getTrainingCost(TurnExecutionContext context) {
        var combatBalance = context.gameBalance().combat();
        var config = getCombatUnitConfig(combatBalance);
        var producedUnits = getProducedUnitsAmount(config);
        var metalCost = config.getCost() * producedUnits;
        return new ResourceSet(metalCost, 0, 0);
    }

    private int getProducedUnitsAmount(CombatUnitConfig config) {
        return switch (trainingMode) {
            case SMALL_PRODUCTION -> config.getSmallProduction();
            case MEDIUM_PRODUCTION -> config.getMediumProduction();
            case MASS_PRODUCTION -> config.getMassProduction();
        };
    }

    private CombatUnitConfig getCombatUnitConfig(AllCombatBalanceDto combatBalance) {
        return switch (unitType) {
            case DROID -> combatBalance.droids();
            case TANK -> combatBalance.tanks();
            case CANNON -> combatBalance.cannons();
        };
    }
}
