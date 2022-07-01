package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.RocketStrikeValidRocketStrikeTargetValidatable;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RocketStrikeValidRocketStrikeTargetValidatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private RocketStrikeValidRocketStrikeTargetValidator tested;

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
    void validateHappyPathNotAttackingRocket() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.HOME_BASE), "BB");
        var validatables = List.of(
                new RocketStrikeValidRocketStrikeTargetValidatable(new Location(1, 1), false)
        );
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateNoValidatables() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.HOME_BASE), "BB");
        var validatables = new ArrayList<RocketStrikeValidRocketStrikeTargetValidatable>();
        assertTrue(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathAttackingRocket() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.HOME_BASE), "BB");
        var validatables = List.of(
                new RocketStrikeValidRocketStrikeTargetValidatable(new Location(1, 1), true)
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateUnhappyPathAttackingRocketOutOfBounds() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "BB");
        var validatables = List.of(
                new RocketStrikeValidRocketStrikeTargetValidatable(new Location(1, -99), true)
        );
        assertFalse(tested.validate(validatables, context));
    }

    @Test
    void validateHappyPathAttackingRocket() {
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "BB");
        var validatables = List.of(
                new RocketStrikeValidRocketStrikeTargetValidatable(new Location(1, 1), true)
        );
        assertTrue(tested.validate(validatables, context));
    }
}