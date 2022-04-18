package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.BuildingUpgradableValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuildingUpgradableValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private BuildingUpgradableValidator tested;

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        var validatables = List.of(
                new BuildingUpgradableValidatable(context.game().getFields()[0][0].getBuilding()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][1].getBuilding()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][2].getBuilding())
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateTryingToUpgradeEmptyField() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        var validatables = List.of(
                new BuildingUpgradableValidatable(context.game().getFields()[0][0].getBuilding()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][1].getBuilding()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][2].getBuilding())
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateTryingToUpgradeToNonExistingLevel() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY, 4), "AA");
        var validatables = List.of(
                new BuildingUpgradableValidatable(context.game().getFields()[0][0].getBuilding()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][1].getBuilding()),
                new BuildingUpgradableValidatable(context.game().getFields()[0][2].getBuilding())
        );
        assertFalse(tested.validate(validatables, context));
    }
}