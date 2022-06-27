package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.BuildingSufficientForUnitsTrainingValidatable;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuildingSufficientForUnitsTrainingValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private BuildingSufficientForUnitsTrainingValidator tested;

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
                        BoardUtils.generateEmptyPlain(9),
                        new StateManager(
                                GameState.WAITING_TO_START,
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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE, 3), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.TOWER, 3), "AA");

        var validatables = List.of(
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][0].getBuilding(),
                        UnitType.TANK
                ),
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][1].getBuilding(),
                        UnitType.CANNON
                )
        );
        var result = tested.validate(validatables, context);
        assertTrue(result);
    }

    @Test
    void validateWrongBuildingType() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE, 3), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.METAL_FACTORY, 3), "AA");

        var validatables = List.of(
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][0].getBuilding(),
                        UnitType.TANK
                ),
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][1].getBuilding(),
                        UnitType.CANNON
                )
        );
        var result = tested.validate(validatables, context);
        assertFalse(result);
    }

    @Test
    void validateLevelTooLowForTanks() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE, 1), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.TOWER, 3), "AA");

        var validatables = List.of(
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][0].getBuilding(),
                        UnitType.TANK
                ),
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][1].getBuilding(),
                        UnitType.CANNON
                )
        );
        var result = tested.validate(validatables, context);
        assertFalse(result);
    }

    @Test
    void validateLevelTooLowForCannons() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE, 3), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.TOWER, 2), "AA");

        var validatables = List.of(
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][0].getBuilding(),
                        UnitType.TANK
                ),
                new BuildingSufficientForUnitsTrainingValidatable(
                        context.game().getFields()[0][1].getBuilding(),
                        UnitType.CANNON
                )
        );
        var result = tested.validate(validatables, context);
        assertFalse(result);
    }

    //todo: finish this and add other validators tests
}