package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors.components;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.BattleResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat.FightingArmy;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.FieldConquestService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldConquestServiceTest {

    @Autowired
    private FieldConquestService tested;

    private TurnExecutionContext context;

    @Autowired
    private GameBalanceService gameBalanceService;

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
                                GameState.AWAITING,
                                DateUtils.millisecondsFromNow(10_000),
                                "AA",
                                "TIMEOUTID"
                        )
                ),
                player
        );
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.TOWER), "BB");
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY), "AA");
    }

    @Test
    void executeOptionalFieldConquestAttackersHaveWonAgainstPlayer() {
        context.game().setEventQueue(List.of(
                new BuildBuildingEvent(new Location(0, 0), BuildingType.TOWER),
                new BuildBuildingEvent(new Location(1, 1), BuildingType.ELECTRICITY_FACTORY),
                new ArmyTrainingEvent(2, new Location(0, 1), UnitType.TANK, 4),
                new ArmyTrainingEvent(2, new Location(0, 0), UnitType.TANK, 4)
        ));
        var battleResult = new BattleResult(
                new FightingArmy(100, 20, 40, 0),
                new FightingArmy(10, 4, 10, 0),
                new FightingArmy(70, 14, 28, 0),
                new FightingArmy(0, 0, 0, 0),
                true,
                false
        );
        var newContext = tested.executeOptionalFieldConquest(Pair.of(new Location(0, 0), battleResult), context);
        var conqueredField = newContext.game().getFields()[0][0];
        assertEquals("AA", conqueredField.getOwnerId());
        assertEquals(new Army(70, 14, 28), conqueredField.getArmy());

        var expectedEventQueue = List.of(
                new BuildBuildingEvent(new Location(1, 1), BuildingType.ELECTRICITY_FACTORY),
                new ArmyTrainingEvent(2, new Location(0, 1), UnitType.TANK, 4)
        );
        assertThat(expectedEventQueue, sameBeanAs(newContext.game().getEventQueue()));
    }

    @Test
    void executeOptionalFieldConquestAttackersHaveLostAgainstPlayer() {
        var battleResult = new BattleResult(
                new FightingArmy(100, 20, 40, 0),
                new FightingArmy(200, 200, 200, 0),
                new FightingArmy(0, 0, 0, 0),
                new FightingArmy(180, 180, 180, 0),
                false,
                false
        );
        var newContext = tested.executeOptionalFieldConquest(Pair.of(new Location(0, 0), battleResult), context);
        var conqueredField = newContext.game().getFields()[0][0];
        assertEquals("BB", conqueredField.getOwnerId());
        assertEquals(new Army(180, 180, 180), conqueredField.getArmy());
    }

    @Test
    void executeOptionalFieldConquestAttackersHaveWonAgainstScarabs() {
        var battleResult = new BattleResult(
                new FightingArmy(100, 20, 40, 0),
                new FightingArmy(0, 0, 0, 200),
                new FightingArmy(70, 14, 28, 0),
                new FightingArmy(0, 0, 0, 0),
                true,
                false
        );
        var newContext = tested.executeOptionalFieldConquest(Pair.of(new Location(0, 0), battleResult), context);
        var conqueredField = newContext.game().getFields()[0][0];
        assertEquals("AA", conqueredField.getOwnerId());
        assertEquals(new Army(70, 14, 28), conqueredField.getArmy());
    }

    @Test
    void executeOptionalFieldConquestAttackersHaveLostAgainstScarabs() {
        context.game().getFields()[0][0].setOwnerId(null);
        var battleResult = new BattleResult(
                new FightingArmy(10, 2, 4, 0),
                new FightingArmy(0, 0, 0, 200),
                new FightingArmy(0, 0, 0, 0),
                new FightingArmy(0, 0, 0, 80),
                false,
                false
        );
        var newContext = tested.executeOptionalFieldConquest(Pair.of(new Location(0, 0), battleResult), context);
        var conqueredField = newContext.game().getFields()[0][0];
        assertNull(conqueredField.getOwnerId());
        assertEquals(new Army(0, 0, 0), conqueredField.getArmy());
    }

}