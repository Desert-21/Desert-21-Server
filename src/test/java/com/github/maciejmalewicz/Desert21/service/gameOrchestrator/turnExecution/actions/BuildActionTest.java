package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
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
class BuildActionTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;
    private Player player;

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
    }

    @Test
    void getActionValidatablesHappyPath() throws NotAcceptableException {
        var buildAction = new BuildAction(new Location(0, 0), BuildingType.METAL_FACTORY);
        var validatables = buildAction.getActionValidatables(context);
        var expectedValidatables = List.of(
                new CostValidatable(new ResourceSet(0, 600, 0)),
                new FieldOwnershipValidatable(context.game().getFields()[0][0], player),
                new IsFieldEmptyValidatable(context.game().getFields()[0][0]),
                new HasUpgradeRequiredToBuildValidatable(BuildingType.METAL_FACTORY),
                new IsBuildingBuildableValidatable(BuildingType.METAL_FACTORY),
                new SingleBuildPerLocationValidatable(new Location(0, 0))
        );
        assertThat(expectedValidatables, sameBeanAs(validatables));
    }

    @Test
    void getActionValidatablesLocationOutOfBounds() throws NotAcceptableException {
        var buildAction = new BuildAction(new Location(0, -99), BuildingType.METAL_FACTORY);
        var exception = assertThrows(NotAcceptableException.class, () -> {
            buildAction.getActionValidatables(context);
        });
        assertEquals("Selected field is not within board bounds!", exception.getMessage());
    }

    @Test
    void getEventExecutables() throws NotAcceptableException {
        var buildAction = new BuildAction(new Location(0, 0), BuildingType.METAL_FACTORY);
        var events = buildAction.getEventExecutables(context);
        var expectedEvents = List.of(
                new PaymentEvent(new ResourceSet(0, 600, 0)),
                new BuildBuildingEvent(new Location(0, 0), BuildingType.METAL_FACTORY)
        );
        assertThat(expectedEvents, sameBeanAs(events));
    }
}