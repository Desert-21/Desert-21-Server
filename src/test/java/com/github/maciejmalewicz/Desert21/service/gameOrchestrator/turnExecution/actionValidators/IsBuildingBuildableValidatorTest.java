package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsBuildingBuildableValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IsBuildingBuildableValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private IsBuildingBuildableValidator tested;

    @BeforeEach
    void setup() {
        var player = new Player("AA",
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
    void validateHappyPath() {
        var validatables = List.of(
                new IsBuildingBuildableValidatable(BuildingType.METAL_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.BUILDING_MATERIALS_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.ELECTRICITY_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.TOWER)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPath() {
        var validatables = List.of(
                new IsBuildingBuildableValidatable(BuildingType.METAL_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.BUILDING_MATERIALS_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.ELECTRICITY_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.TOWER),
                new IsBuildingBuildableValidatable(BuildingType.ROCKET_LAUNCHER),
                new IsBuildingBuildableValidatable(BuildingType.HOME_BASE)
        );
        assertFalse(tested.validate(validatables, context));
    }
}