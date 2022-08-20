package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.RocketStrikeDestroysRocketLauncherEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.RocketStrikeEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.RocketStrikeEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RocketStrikeExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private RocketStrikeExecutor tested;
    private TurnExecutionContext context;

    @BeforeEach
    void setup() {
        var player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        tested = new RocketStrikeExecutor();
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
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[1][0] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[1][1] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "AA");

        context.game().getFields()[5][5] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
    }

    @Test
    void executeHappyPathAgainstArmy() throws NotAcceptableException {
        context.game().getFields()[5][5].setArmy(new Army(10, 10, 10));
        var events = List.of(
                new RocketStrikeEvent(new Location(5, 5), false)
        );
        var eventExecutionResult = tested.execute(events, context);

        var expectedResults = List.of(
                new RocketStrikeEventResult(
                        new Location(5, 5),
                        new Army(10, 10, 10),
                        new Army(5, 5, 5)
                )
        );
        assertThat(expectedResults, sameBeanAs(eventExecutionResult.results()));

        var updatedContext = eventExecutionResult.context();
        var currentArmy = updatedContext.game().getFields()[5][5].getArmy();
        var expectedArmy = new Army(5, 5, 5);
        assertThat(expectedArmy, sameBeanAs(currentArmy));
        assertEquals(BuildingType.ROCKET_LAUNCHER, updatedContext.game().getFields()[5][5].getBuilding().getType());
        assertEquals(1, updatedContext.player().getRocketStrikesDone());
    }

    @Test
    void executeHappyPathAgainstArmyNull() throws NotAcceptableException {
        context.game().getFields()[5][5].setArmy(null);
        var events = List.of(
                new RocketStrikeEvent(new Location(5, 5), false)
        );
        var eventExecutionResult = tested.execute(events, context);

        var expectedResults = List.of(
                new RocketStrikeEventResult(new Location(5, 5), null, null)
        );
        assertThat(expectedResults, sameBeanAs(eventExecutionResult.results()));

        var updatedContext = eventExecutionResult.context();
        var currentArmy = updatedContext.game().getFields()[5][5].getArmy();
        assertThat(null, sameBeanAs(currentArmy));
        assertEquals(BuildingType.ROCKET_LAUNCHER, updatedContext.game().getFields()[5][5].getBuilding().getType());
        assertEquals(1, updatedContext.player().getRocketStrikesDone());
    }

    @Test
    void executeHappyPathAgainstRocketLauncher() throws NotAcceptableException {
        context.game().getFields()[5][5].setArmy(new Army(10, 10, 10));
        var events = List.of(
                new RocketStrikeEvent(new Location(5, 5), true)
        );
        var eventExecutionResult = tested.execute(events, context);

        var expectedResults = List.of(
                new RocketStrikeDestroysRocketLauncherEventResult(new Location(5, 5))
        );
        assertThat(expectedResults, sameBeanAs(eventExecutionResult.results()));

        var updatedContext = eventExecutionResult.context();
        var currentArmy = updatedContext.game().getFields()[5][5].getArmy();
        var expectedArmy = new Army(10, 10, 10);
        assertThat(expectedArmy, sameBeanAs(currentArmy));
        assertEquals(BuildingType.EMPTY_FIELD, updatedContext.game().getFields()[5][5].getBuilding().getType());
        assertEquals(1, updatedContext.player().getRocketStrikesDone());
    }
}