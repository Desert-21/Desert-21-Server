package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.notifications.contents.turnResolution.ResourcesProducedNotification;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.ResourcesProducedResult;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.RESOURCES_PRODUCED_NOTIFICATION;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResourcesProductionExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private ResourcesProductionExecutor tested;
    private TurnExecutionContext context;

    @BeforeEach
    void setup() {
        var player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        tested = new ResourcesProductionExecutor();
        context = new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                new Game(
                        List.of(
                                player,
                                new Player("BB",
                                        "schabina123456",
                                        new ResourceSet(60, 60, 60))),
                        BoardUtils.generateEmptyPlain(9),
                        new StateManager(
                                GameState.AWAITING,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
        //regular lvl 1 set
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.METAL_FACTORY), "AA");
        context.game().getFields()[1][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "AA");
    }

    @Test
    void executeWithoutAnyUpgrades() throws NotAcceptableException {
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(41, 41, 41), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());
        var forBothNotifications = productionResult.forBoth();
        assertEquals(1, forBothNotifications.size());
        var forBothNotification = forBothNotifications.get(0);
        assertEquals(RESOURCES_PRODUCED_NOTIFICATION, forBothNotification.type());
        var notificationContent = (ResourcesProducedNotification) forBothNotification.content();
        assertEquals(1000, notificationContent.getMillisecondsToView());
        assertEquals(new ResourceSet(41, 41, 41), notificationContent.getProduced());
        assertEquals("AA", notificationContent.getPlayerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(101, 101, 101), newPlayer.getResources());
    }

    @Test
    void executeWithoutAnyUpgradesOnDifferentLevels() throws NotAcceptableException {
        context.game().getFields()[0][1].getBuilding().setLevel(2);
        context.game().getFields()[1][0].getBuilding().setLevel(3);
        context.game().getFields()[1][1].getBuilding().setLevel(4);
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(51, 71, 101), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(111, 131, 161), newPlayer.getResources());
    }

    @Test
    void executeWithMoreResourceUpgrades() throws NotAcceptableException {
        context.player().getOwnedUpgrades().addAll(List.of(
                LabUpgrade.MORE_METAL,
                LabUpgrade.MORE_BUILDING_MATERIALS,
                LabUpgrade.MORE_ELECTRICITY
        ));
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(56, 56, 56), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(116, 116, 116), newPlayer.getResources());
    }

    @Test
    void executeWithGoldDiggersUpgrade() throws NotAcceptableException {
        context.player().getOwnedUpgrades().addAll(List.of(
                LabUpgrade.GOLD_DIGGERS
        ));
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(47, 47, 47), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(107, 107, 107), newPlayer.getResources());
    }

    @Test
    void executeWithProductionManagersUpgrade() throws NotAcceptableException {
        context.player().getOwnedUpgrades().addAll(List.of(
                LabUpgrade.PRODUCTION_MANAGERS
        ));
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(49, 49, 49), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(109, 109, 109), newPlayer.getResources());
    }

    @Test
    void executeWithHomeSweetHomeUpgrade() throws NotAcceptableException {
        context.player().getOwnedUpgrades().addAll(List.of(
                LabUpgrade.HOME_SWEET_HOME
        ));
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(51, 51, 51), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(111, 111, 111), newPlayer.getResources());
    }

    @Test
    void executeWithAIUpgrade() throws NotAcceptableException {
        context.player().getOwnedUpgrades().addAll(List.of(
                LabUpgrade.PRODUCTION_AI
        ));
        var productionAI = new ProductionAI();
        productionAI.setCurrentProduction(33);
        productionAI.setActivated(true);
        context.player().setProductionAI(productionAI);
        //always executed on empty list
        var eventExecutionResults = tested.execute(new ArrayList<>(), context);

        var results = eventExecutionResults.results();
        assertEquals(1, results.size());
        var productionResult = (ResourcesProducedResult) results.get(0);
        assertEquals(1000, productionResult.millisecondsToView());
        assertEquals(new ResourceSet(74, 74, 74), productionResult.resourceSet());
        assertEquals("AA", productionResult.playerId());

        var newPlayer = eventExecutionResults.context().player();
        assertEquals(new ResourceSet(134, 134, 134), newPlayer.getResources());
    }
}