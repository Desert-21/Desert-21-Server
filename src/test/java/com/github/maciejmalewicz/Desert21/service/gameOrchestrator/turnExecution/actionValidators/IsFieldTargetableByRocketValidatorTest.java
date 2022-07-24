package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.IsFieldTargetableByRocketValidatable;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IsFieldTargetableByRocketValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private IsFieldTargetableByRocketValidator tested;

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.TOWER, 2), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), null);
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.HOME_BASE, 4), "AA");
    }

    @Test
    void validateHappyPathNotDefensive() {
        var validatables = List.of(new IsFieldTargetableByRocketValidatable(new Location(0, 1)));
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathDefensiveButLowerLevel() {
        var validatables = List.of(new IsFieldTargetableByRocketValidatable(new Location(0, 0)));
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathDefensiveLevel4() {
        var validatables = List.of(new IsFieldTargetableByRocketValidatable(new Location(0, 2)));
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathLocationOutOfBounds() {
        var validatables = List.of(new IsFieldTargetableByRocketValidatable(new Location(0, 99)));
        assertFalse(tested.validate(validatables, context));
    }
}