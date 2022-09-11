package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BombardingEvent;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BombardingBattleExecutorTest {
    @Autowired
    private GameBalanceService gameBalanceService;

    private Player player;
    private TurnExecutionContext context;

    @Autowired
    private BombardingBattleExecutor tested;

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
        context.game().getFields()[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "BB");
    }

    @Test
    void executeBombardingAttackersWin() throws NotAcceptableException {
        var bombardingEvent = new BombardingEvent(new Location(0, 0), 40);
        context.game().getFields()[0][0].setArmy(new Army(10, 2, 5));
        /*
        attackers power: 1000
        defenders power: 300 + 440 + 250 = 990
         */
        var battleResult = tested.executeBombarding(bombardingEvent, context);
        assertTrue(battleResult.haveAttackersWon());
        assertFalse(battleResult.wasUnoccupied());
        assertEquals(new FightingArmy(0, 0, 40, 0), battleResult.attackersBefore());
        assertEquals(new FightingArmy(0, 0, 40, 0), battleResult.attackersAfter());
        assertEquals(new FightingArmy(10, 2, 5, 0), battleResult.defendersBefore());
        assertEquals(new FightingArmy(0, 0, 0, 0), battleResult.defendersAfter());
    }

    @Test
    void executeBombardingAttackersWinShouldIgnoreKingOfsDesert() throws NotAcceptableException {
        player.getOwnedUpgrades().add(LabUpgrade.KING_OF_DESERT);
        var bombardingEvent = new BombardingEvent(new Location(0, 0), 40);
        context.game().getFields()[0][0].setArmy(new Army(10, 2, 5));
        /*
        attackers power: 1000
        defenders power: 300 + 440 + 250 = 990
         */
        var battleResult = tested.executeBombarding(bombardingEvent, context);
        assertTrue(battleResult.haveAttackersWon());
        assertFalse(battleResult.wasUnoccupied());
        assertEquals(new FightingArmy(0, 0, 40, 0), battleResult.attackersBefore());
        assertEquals(new FightingArmy(0, 0, 40, 0), battleResult.attackersAfter());
        assertEquals(new FightingArmy(10, 2, 5, 0), battleResult.defendersBefore());
        assertEquals(new FightingArmy(0, 0, 0, 0), battleResult.defendersAfter());
    }

    @Test
    void executeBombardingDefendersWin() throws NotAcceptableException {
        var bombardingEvent = new BombardingEvent(new Location(0, 0), 20);
        context.game().getFields()[0][0].setArmy(new Army(10, 2, 5));
        /*
        attackers power: 500
        defenders power: 300 + 440 + 250 = 990
         */
        var battleResult = tested.executeBombarding(bombardingEvent, context);
        assertFalse(battleResult.haveAttackersWon());
        assertFalse(battleResult.wasUnoccupied());
        assertEquals(new FightingArmy(0, 0, 20, 0), battleResult.attackersBefore());
        assertEquals(new FightingArmy(0, 0, 20, 0), battleResult.attackersAfter());
        assertEquals(new FightingArmy(10, 2, 5, 0), battleResult.defendersBefore());
        assertEquals(new FightingArmy(6, 2, 3, 0), battleResult.defendersAfter());
    }
}