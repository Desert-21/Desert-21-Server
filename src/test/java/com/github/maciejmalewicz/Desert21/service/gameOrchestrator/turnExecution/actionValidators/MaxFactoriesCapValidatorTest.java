package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.MaxFactoriesCapValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MaxFactoriesCapValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private Player player;
    private TurnExecutionContext context;

    @Autowired
    private MaxFactoriesCapValidator tested;

    @BeforeEach
    void setup() {
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        context = new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                new Game(
                        List.of(
                                player,
                                new Player("BB",
                                        "schabina123456",
                                        new ResourceSet(60, 60, 60))),
                        new Field[9][9],
                        new StateManager(
                                GameState.AWAITING,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
    }

    @Test
    void validateHappyPathNoFactoriesBuiltBefore() {
        var validatables = List.of(
                new MaxFactoriesCapValidatable(),
                new MaxFactoriesCapValidatable()
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathMixTypesOfFactoriesBuiltFactors() {
        player.setBuiltFactories(1);
        context.game().setEventQueue(List.of(new BuildBuildingEvent(new Location(0, 0), BuildingType.METAL_FACTORY)));
        var validatables = List.of(
                new MaxFactoriesCapValidatable(),
                new MaxFactoriesCapValidatable()
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathMixTypesOfFactoriesBuiltFactors() {
        player.setBuiltFactories(2);
        context.game().setEventQueue(List.of(
                new BuildBuildingEvent(new Location(0, 0), BuildingType.METAL_FACTORY),
                new BuildBuildingEvent(new Location(0, 0), BuildingType.BUILDING_MATERIALS_FACTORY),
                new BuildBuildingEvent(new Location(0, 0), BuildingType.ELECTRICITY_FACTORY)
                ));
        var validatables = List.of(
                new MaxFactoriesCapValidatable(),
                new MaxFactoriesCapValidatable()
        );
        assertFalse(tested.validate(validatables, context));
    }
}