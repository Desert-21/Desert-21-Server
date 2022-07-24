package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.*;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
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
class FireRocketActionTest {

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.HOME_BASE), "AA");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.BUILDING_MATERIALS_FACTORY), "AA");
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        context.game().getFields()[0][3] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        context.game().getFields()[0][4] = new Field(new Building(BuildingType.EMPTY_FIELD), "BB");
        context.game().getFields()[0][5] = new Field(new Building(BuildingType.EMPTY_FIELD), "BB");
    }

    @Test
    void getActionValidatables() throws NotAcceptableException {
        var rocketAction = new FireRocketAction(new Location(1, 1), false);
        var validatables = rocketAction.getActionValidatables(context);
        var ownedFields = List.of(
                context.game().getFields()[0][0],
                context.game().getFields()[0][1],
                context.game().getFields()[0][2],
                context.game().getFields()[0][3]
        );
        var expectedValidatables = List.of(
                new SingleRocketStrikePerTurnValidatable(),
                new CostValidatable(new ResourceSet(0, 0, 300)),
                new RocketLauncherOwnershipValidatable(ownedFields),
                new IsFieldTargetableByRocketValidatable(new Location(1, 1)),
                new RocketStrikeValidRocketStrikeTargetValidatable(new Location(1, 1), false),
                new SuperSonicUpgradeValidatable(false)
        );
        assertThat(expectedValidatables, sameBeanAs(validatables));
    }

    @Test
    void getActionValidatablesRocketStrikesPassedAndTwoLaunchersOwned() throws NotAcceptableException {
        context.game().getFields()[1][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        player.setRocketStrikesDone(4);
        var rocketAction = new FireRocketAction(new Location(1, 1), false);
        var validatables = rocketAction.getActionValidatables(context);
        var ownedFields = List.of(
                context.game().getFields()[0][0],
                context.game().getFields()[0][1],
                context.game().getFields()[0][2],
                context.game().getFields()[0][3],
                context.game().getFields()[1][0]
        );
        var expectedValidatables = List.of(
                new SingleRocketStrikePerTurnValidatable(),
                new CostValidatable(new ResourceSet(0, 0, 550)),
                new RocketLauncherOwnershipValidatable(ownedFields),
                new IsFieldTargetableByRocketValidatable(new Location(1, 1)),
                new RocketStrikeValidRocketStrikeTargetValidatable(new Location(1, 1), false),
                new SuperSonicUpgradeValidatable(false)
        );
        assertThat(expectedValidatables, sameBeanAs(validatables));
    }

    @Test
    void getEventExecutablesHappyPath() throws NotAcceptableException {
        var rocketAction = new FireRocketAction(new Location(1, 1), false);
        var events = rocketAction.getEventExecutables(context);
        var expectedEvents = List.of(
                new PaymentEvent(new ResourceSet(0, 0, 300)),
                new RocketStrikeEvent(new Location(1, 1), false)
        );
        assertThat(expectedEvents, sameBeanAs(events));
    }

    @Test
    void getEventExecutablesRocketStrikesPassedAndTwoLaunchersOwned() throws NotAcceptableException {
        context.game().getFields()[1][0] = new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA");
        player.setRocketStrikesDone(5);
        var rocketAction = new FireRocketAction(new Location(1, 1), false);
        var events = rocketAction.getEventExecutables(context);
        var expectedEvents = List.of(
                new PaymentEvent(new ResourceSet(0, 0, 650)),
                new RocketStrikeEvent(new Location(1, 1), false)
        );
        assertThat(expectedEvents, sameBeanAs(events));
    }
}