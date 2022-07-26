package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.BuildingBuiltEventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
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
class BuildBuildingExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    @Autowired
    private BuildBuildingExecutor tested;

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
    }

    @Test
    void executeHappyPath() throws NotAcceptableException {
        var events = List.of(
                new BuildBuildingEvent(new Location(0, 0), BuildingType.METAL_FACTORY),
                new BuildBuildingEvent(new Location(0, 1), BuildingType.TOWER)
        );
        var eventExecutionResult = tested.execute(events, context);

        var results = eventExecutionResult.results();
        var expectedResults = List.of(
                new BuildingBuiltEventResult(new Location(0, 0), BuildingType.METAL_FACTORY),
                new BuildingBuiltEventResult(new Location(0, 1), BuildingType.TOWER)
        );
        assertThat(expectedResults, sameBeanAs(results));

        assertEquals(
                BuildingType.METAL_FACTORY,
                context.game().getFields()[0][0].getBuilding().getType()
        );
        assertEquals(
                BuildingType.TOWER,
                context.game().getFields()[0][1].getBuilding().getType()
        );
        assertEquals(1, context.player().getBuiltTowers());
        assertEquals(1, context.player().getBuiltFactories());
    }

    @Test
    void executeLocationOutOfBounds() {
        var events = List.of(
                new BuildBuildingEvent(new Location(0, 0), BuildingType.METAL_FACTORY),
                new BuildBuildingEvent(new Location(0, 99), BuildingType.TOWER)
        );
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.execute(events, context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }
}