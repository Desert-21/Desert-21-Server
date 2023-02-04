package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ArmyPowerCalculatorTest {

    private ArmyPowerCalculator tested;

    private ScarabsPowerCalculator scarabsPowerCalculator;

    @Autowired
    private GameBalanceService gameBalanceService;

    private TurnExecutionContext context;

    private Player player;
    private Player opponent;

    void setupContext() {
        player = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
        opponent = new Player("BB",
                "schabina123456",
                new ResourceSet(60, 60, 60));
        context = new TurnExecutionContext(
                gameBalanceService.getGameBalance(),
                new Game(
                        List.of(
                                player,
                                opponent),
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
        context.game().getFields()[0][1] = new Field(new Building(BuildingType.TOWER, 2), "BB");
        context.game().getFields()[0][2] = new Field(new Building(BuildingType.ELECTRICITY_FACTORY, 1), "BB");
    }

    void setupTested() {
        scarabsPowerCalculator = mock(ScarabsPowerCalculator.class);
        tested = new ArmyPowerCalculator(
            scarabsPowerCalculator
        );
    }

    @BeforeEach
    void setup() {
        setupContext();
        setupTested();
    }

    @Test
    void calculateAttackersPowerNoPerks() {
        var attackers = new FightingArmy(100, 10, 20, 0);
        // 3000 + 2200 + 1000 = 6200
        var power = tested.calculateAttackersPower(attackers, context);
        assertEquals(6200, power);
    }

    @Test
    void calculateAttackersPowerImprovedTanks() {
        player.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);
        var attackers = new FightingArmy(100, 10, 20, 0);
        // 3000 + 2200 * 1.5 + 1000 = 3000 + 3300 + 1000 = 7300
        var power = tested.calculateAttackersPower(attackers, context);
        assertEquals(7300, power);
    }

    @Test
    void calculateDefendersPowerNoPerks() throws NotAcceptableException {
        var defenders = new FightingArmy(100, 10, 20, 0);
        // 3000 + 2200 + 1000 = 6200
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][0],
                new Army(0, 0, 0)
        );
        assertEquals(6200, power);
    }

    @Test
    void calculateDefendersPowerHasTower() throws NotAcceptableException {
        var defenders = new FightingArmy(100, 10, 20, 0);
        // base army power: 3000 + 2200 + 1000 = 6200
        // tower base bonus: 1000
        // tower unit bonus: 0.3
        // total power: 1000 + 6200 * 1.3 = 1000 + 8060 = 9060
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][1],
                new Army(0, 0, 0)
        );
        assertEquals(9060, power);
    }

    @Test
    void calculateDefendersPowerHasFactoryTurret() throws NotAcceptableException {
        opponent.getOwnedUpgrades().add(LabUpgrade.FACTORY_TURRET);
        var defenders = new FightingArmy(100, 10, 20, 0);
        // base army power: 3000 + 2200 + 1000 = 6200
        // tower base bonus: 1000
        // tower unit bonus: 0.3
        // total power: 1000 + 6200 * 1.3 = 1000 + 8060 = 9060
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][2],
                new Army(0, 0, 0)
        );
        assertEquals(9060, power);
    }

    @Test
    void calculateDefendersPowerHasImprovedDroidsOnNonDefensiveBuilding() throws NotAcceptableException {
        opponent.getOwnedUpgrades().add(LabUpgrade.IMPROVED_DROIDS);
        var defenders = new FightingArmy(100, 10, 20, 0);
        // 3000 * 1.25 + 2200 + 1000 = 3750 + 2200 + 1000 = 6950
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][2],
                new Army(0, 0, 0)
        );
        assertEquals(6950, power);
    }

    @Test
    void calculateDefendersPowerHasImprovedDroidsOnTower() throws NotAcceptableException {
        opponent.getOwnedUpgrades().add(LabUpgrade.IMPROVED_DROIDS);
        var defenders = new FightingArmy(100, 10, 20, 0);
        // 3000 * 1.4 + 2200 + 1000 = 4200 + 2200 + 1000 = 7400
        // tower base bonus: 1000
        // tower unit bonus: 0.3
        // total power: 1000 + 7400 * 1.3 = 1000 + 9620 = 10 620
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][1],
                new Army(0, 0, 0)
        );
        assertEquals(10_620, power);
    }

    @Test
    void calculateDefendersPowerHasImprovedTanks() throws NotAcceptableException {
        opponent.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);
        var defenders = new FightingArmy(100, 10, 20, 0);
        // 3000 + 2200 * 1.5 + 1000 = 3000 + 3300 + 1000 = 7300
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][0],
                new Army(0, 0, 0)
        );
        assertEquals(7300, power);
    }

    @Test
    void calculateDefendersPowerOpponentHasAdvancedTacticsOnTower() throws NotAcceptableException {
        player.getOwnedUpgrades().add(LabUpgrade.ADVANCED_TACTICS);
        var defenders = new FightingArmy(100, 10, 20, 0);
        // base army power: 3000 + 2200 + 1000 = 6200
        // tower base bonus: 1000
        // tower base bonus after advanced tactics decrease: 300
        // tower unit bonus: 0.3
        // tower unit bonus after advanced tactics decrease: 0.09
        // NOT ACTUAL total power: 1000 + 6200 * 1.3 = 1000 + 8060 = 9060
        // ACTUAL total power: 300 + 6200 * 1.09 = 300 + 6758 = 7058
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][1],
                new Army(1, 1, 1) // all types of units used
        );
        assertEquals(7058, power);
    }

    @Test
    void calculateDefendersPowerOpponentHasAdvancedTacticsOnFactoryTurret() throws NotAcceptableException {
        opponent.getOwnedUpgrades().add(LabUpgrade.FACTORY_TURRET);
        player.getOwnedUpgrades().add(LabUpgrade.ADVANCED_TACTICS);
        var defenders = new FightingArmy(100, 10, 20, 0);
        // base army power: 3000 + 2200 + 1000 = 6200
        // factory turret base bonus: 1000
        // factory turret base bonus after advanced tactics decrease: 300
        // factory turret unit bonus: 0.3
        // factory turret unit bonus after advanced tactics decrease: 0.09
        // NOT ACTUAL total power: 1000 + 6200 * 1.3 = 1000 + 8060 = 9060
        // ACTUAL total power: 300 + 6200 * 1.09 = 300 + 6758 = 7058
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][2],
                new Army(1, 1, 1) // all types of units used
        );
        assertEquals(7058, power);
    }

    @Test
    void calculateDefendersPowerHasKingOfDesertScarabGeneration() throws NotAcceptableException {
        doReturn(600).when(scarabsPowerCalculator).calculateScarabsPower(60, context);
        var defenders = new FightingArmy(100, 10, 20, 60);
        // 3000 + 2200 + 1000 + 600 = 6800
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][0],
                new Army(0, 0, 0)
        );
        assertEquals(6800, power);
    }

    @Test
    void calculateDefendersPowerHasAllPerks() throws NotAcceptableException {
        opponent.getOwnedUpgrades().add(LabUpgrade.IMPROVED_DROIDS);
        opponent.getOwnedUpgrades().add(LabUpgrade.IMPROVED_TANKS);

        player.getOwnedUpgrades().add(LabUpgrade.ADVANCED_TACTICS);

        doReturn(600).when(scarabsPowerCalculator).calculateScarabsPower(60, context);
        var defenders = new FightingArmy(100, 10, 20, 60);

        // base power: 3000, 2200, 1000
        // tower base bonus: 1000
        // tower unit bonus: 0.3
        // tower base decreased bonus: 300
        // tower unit decreased bonus: 0.09
        // after tower bonuses: 3270, 2398, 1090 + 300 bonus
        // after improved droids: 4578, 2398, 1090 + 300 bonus
        // after improved tanks: 4578, 3597, 1090 + 300 bonus
        // total base army power: 4578 + 3597 + 1090 + 300 = 9565
        // power with scarabs: 9565 + 600 = 10 165
        var power = tested.calculateDefendersPower(
                defenders,
                context,
                opponent,
                player,
                context.game().getFields()[0][1],
                new Army(1, 1, 1)
        );
        assertEquals(10_165, power);
    }
}