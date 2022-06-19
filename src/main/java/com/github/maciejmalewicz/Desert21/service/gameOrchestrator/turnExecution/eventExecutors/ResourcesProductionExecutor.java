package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.FactoryConfig;
import com.github.maciejmalewicz.Desert21.models.turnExecution.EventExecutionResult;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.ResourcesProducedResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ResourcesProductionEvent;
import com.github.maciejmalewicz.Desert21.utils.BuildingUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ResourcesProductionExecutor implements EventExecutor<ResourcesProductionEvent> {

    @Override
    public EventExecutionResult execute(List<ResourcesProductionEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var game = context.game();
        var player = context.player();

        var productionAccumulator = getHomeBaseProduction(context);

        var playersFields = Arrays.stream(game.getFields())
                .flatMap(Arrays::stream)
                .filter(field -> player.getId().equals(field.getOwnerId()))
                .toList();

        var producedByFields = getProductionOfFieldsWithoutBuildings(playersFields, context);
        productionAccumulator = productionAccumulator.add(producedByFields);

        var producedByFactories = getProductionOfFactories(playersFields, context);
        productionAccumulator = productionAccumulator.add(producedByFactories);

        var producedByAi = getAIProduction(context);
        productionAccumulator = productionAccumulator.add(producedByAi);

        productionAccumulator = appendProductionManagersUpgrade(productionAccumulator, context);

        //actually applying changes to the game
        player.setResources(player.getResources().add(productionAccumulator));

        return new EventExecutionResult(context, List.of(new ResourcesProducedResult(productionAccumulator, context.player().getId())));
    }

    private ResourceSet getHomeBaseProduction(TurnExecutionContext context) {
        var gameBalance = context.gameBalance();
        var baseProduction = gameBalance.buildings().homeBase().getProduction();
        var potentialBonus = gameBalance.upgrades().production()
                .getProductionBranchConfig()
                .getHomeSweetHomeProductionBonus();
        var productionAfterUpgrade = (int) Math.round(baseProduction * (1 + potentialBonus));
        var productionOfEach =
                context.player().ownsUpgrade(LabUpgrade.HOME_SWEET_HOME) ?
                        productionAfterUpgrade :
                        baseProduction;
        return ResourceSet.ofEachResourceAmount(productionOfEach);
    }

    private ResourceSet getProductionOfFieldsWithoutBuildings(List<Field> fields, TurnExecutionContext context) {
        var gameBalance = context.gameBalance();
        var baseProduction = gameBalance.general().getProductionPerField();
        var goldDiggersBonus = gameBalance.upgrades().control()
                .getControlBranchConfig()
                .getGoldDiggersProductionPerFieldBonus();
        var upgradedProduction = baseProduction + goldDiggersBonus;
        var actualProductionPerField = context.player().ownsUpgrade(LabUpgrade.GOLD_DIGGERS) ?
                upgradedProduction :
                baseProduction;

        var fieldsAmount = fields.size() - 1; //excluding home base
        var produced = fieldsAmount * actualProductionPerField;

        return ResourceSet.ofEachResourceAmount(produced);
    }

    private ResourceSet getProductionOfFactories(List<Field> ownedFields, TurnExecutionContext context) {
        return ownedFields.stream()
                .map(Field::getBuilding)
                .filter(Building::isFactory)
                .map(building -> getProductionOfFactory(building, context))
                .reduce(ResourceSet.ofEachResourceAmount(0), ResourceSet::add);
    }

    private ResourceSet getProductionOfFactory(Building building, TurnExecutionContext context) {
        try {
            var config = (FactoryConfig) BuildingUtils.buildingTypeToConfig(building.getType(), context.gameBalance());
            var production = config.getProduction().getAtLevel(building.getLevel());
            return switch (building.getType()) {
                case METAL_FACTORY -> getMetalFactoryAdjustedProduction(production, context);
                case BUILDING_MATERIALS_FACTORY -> getBuildingMaterialsFactoryAdjustedProduction(production, context);
                case ELECTRICITY_FACTORY -> getElectricityFactoryAdjustedProduction(production, context);
                default -> ResourceSet.ofEachResourceAmount(0);
            };
        } catch (Exception e) {
            return ResourceSet.ofEachResourceAmount(0);
        }
    }

    private ResourceSet getMetalFactoryAdjustedProduction(int baseProduction, TurnExecutionContext context) {
        var ownsMetalUpgrade = context.player().ownsUpgrade(LabUpgrade.MORE_METAL);
        var productionBranch = context.gameBalance().upgrades().production().getProductionBranchConfig();
        var staticBonus = productionBranch.getMoreMetalProductionStaticBonus();
        var relativeBonus = productionBranch.getMoreMetalProductionRelativeBonus();
        var production = ownsMetalUpgrade ?
                (int) Math.round(baseProduction * (1 + relativeBonus)) + staticBonus :
                baseProduction;
        return new ResourceSet(production, 0, 0);
    }

    private ResourceSet getBuildingMaterialsFactoryAdjustedProduction(int baseProduction, TurnExecutionContext context) {
        var ownsBMUpgrade = context.player().ownsUpgrade(LabUpgrade.MORE_BUILDING_MATERIALS);
        var productionBranch = context.gameBalance()
                .upgrades()
                .production()
                .getProductionBranchConfig();
        var staticBonus = productionBranch.getMoreBuildingMaterialsProductionStaticBonus();
        var relativeBonus = productionBranch.getMoreBuildingMaterialsProductionRelativeBonus();
        var production = ownsBMUpgrade ?
                (int) Math.round(baseProduction * (1 + relativeBonus)) + staticBonus :
                baseProduction;
        return new ResourceSet(0, production, 0);
    }

    private ResourceSet getElectricityFactoryAdjustedProduction(int baseProduction, TurnExecutionContext context) {
        var ownsElectricityUpgrade = context.player().ownsUpgrade(LabUpgrade.MORE_ELECTRICITY);
        var productionBranch = context
                .gameBalance()
                .upgrades()
                .production()
                .getProductionBranchConfig();
        var staticBonus = productionBranch.getMoreElectricityProductionStaticBonus();
        var relativeBonus = productionBranch.getMoreElectricityProductionRelativeBonus();
        var production = ownsElectricityUpgrade ?
                (int) Math.round(baseProduction * (1 + relativeBonus)) + staticBonus :
                baseProduction;
        return new ResourceSet(0, 0, production);
    }

    private ResourceSet getAIProduction(TurnExecutionContext context) {
        var ai = context.player().getProductionAI();
        return ai.isActivated() ?
                ResourceSet.ofEachResourceAmount(ai.getCurrentProduction()) :
                ResourceSet.ofEachResourceAmount(0);
    }

    private ResourceSet appendProductionManagersUpgrade(ResourceSet baseTotalResourceSet, TurnExecutionContext context) {
        var ownsUpgrade = context.player().ownsUpgrade(LabUpgrade.PRODUCTION_MANAGERS);
        var productionBonus = context.gameBalance().upgrades().production()
                .getProductionBranchConfig()
                .getProductionManagersProductionBonus();
        return ownsUpgrade ?
                baseTotalResourceSet.multiplyBy(1 + productionBonus) :
                baseTotalResourceSet;
    }
}
