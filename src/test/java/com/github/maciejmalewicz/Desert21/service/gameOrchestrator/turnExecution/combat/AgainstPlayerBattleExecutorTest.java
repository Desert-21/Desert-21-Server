package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
class AgainstPlayerBattleExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private ArmyPowerCalculator armyPowerCalculator;
    private WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator;
    private AttackersArmyAfterAttackCalculator attackersArmyAfterAttackCalculator;
    private DefendersArmyAfterAttackCalculator defendersArmyAfterAttackCalculator;
    private ScarabsGenerator scarabsGenerator;

    private AgainstPlayerBattleExecutor tested;

    private Player player;
    private Player opponent;
    private TurnExecutionContext context;

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
    }

    void setupTested() {
        armyPowerCalculator = mock(ArmyPowerCalculator.class);
        winnersArmyDestructionRatioCalculator = mock(WinnersArmyDestructionRatioCalculator.class);
        attackersArmyAfterAttackCalculator = mock(AttackersArmyAfterAttackCalculator.class);
        defendersArmyAfterAttackCalculator = mock(DefendersArmyAfterAttackCalculator.class);
        scarabsGenerator = mock(ScarabsGenerator.class);

        tested = new AgainstPlayerBattleExecutor(
                armyPowerCalculator,
                winnersArmyDestructionRatioCalculator,
                attackersArmyAfterAttackCalculator,
                defendersArmyAfterAttackCalculator,
                scarabsGenerator
        );
    }

    @BeforeEach
    void setup() {
        setupContext();
        setupTested();
    }

    @Test
    void executeBattleAgainstPlayerWithWasNotFound() throws NotAcceptableException {
        context.game().setPlayers(context.game().getPlayers().stream().filter(p -> p.getId().equals("AA")).toList());
        var exception = assertThrows(NotAcceptableException.class, () -> {
            tested.executeBattleAgainstPlayer(new Army(100, 20, 40), context, context.game().getFields()[0][0]);
        });
        assertEquals("Opponent could not be identified!", exception.getMessage());
    }

    @Test
    void executeBattleAgainstPlayerDefenderNotGeneratingScarabsAndAttackerWinning() throws NotAcceptableException {
        var attackingArmy = new Army(100, 20, 40);
        context.game().getFields()[0][0].setArmy(new Army(50, 10, 20));

        doReturn(10000).when(armyPowerCalculator)
                .calculateAttackersPower(new FightingArmy(100, 20, 40, 0), context);
        doReturn(5000).when(armyPowerCalculator)
                .calculateDefendersPower(
                        new FightingArmy(50, 10, 20, 0),
                        context,
                        opponent,
                        context.game().getFields()[0][0]
                );
        doReturn(0.5).when(winnersArmyDestructionRatioCalculator)
                .calculateDestructionRatio(10000, 5000, gameBalanceService.getGameBalance());
        doReturn(new FightingArmy(50, 10, 20, 0)).when(attackersArmyAfterAttackCalculator).calculateAttackersArmyAfter(
                new FightingArmy(100, 20, 40, 0),
                true,
                0.5,
                player,
                gameBalanceService.getGameBalance()
        );
        doReturn(new FightingArmy(0, 0, 0, 0)).when(defendersArmyAfterAttackCalculator).calculateDefendersArmyAfter(
                new FightingArmy(50, 10 ,20, 0),
                true,
                0.5
        );

        var result = tested.executeBattleAgainstPlayer(attackingArmy, context, context.game().getFields()[0][0]);
        var expectedResult = new BattleResult(
                new FightingArmy(100, 20, 40, 0),
                new FightingArmy(50, 10, 20, 0),
                new FightingArmy(50, 10, 20, 0),
                new FightingArmy(0, 0, 0, 0),
                true,
                false
        );
        assertThat(expectedResult, sameBeanAs(result));
    }

    @Test
    void executeBattleAgainstPlayerDefenderGeneratingScarabsAndAttackerLoosing() throws NotAcceptableException {
        var attackingArmy = new Army(100, 20, 40);
        context.game().getFields()[0][0].setArmy(new Army(500, 100, 200));
        opponent.getOwnedUpgrades().add(LabUpgrade.KING_OF_DESERT);

        doReturn(100).when(scarabsGenerator).generateScarabs(context);

        doReturn(10000).when(armyPowerCalculator)
                .calculateAttackersPower(new FightingArmy(100, 20, 40, 0), context);
        doReturn(50000).when(armyPowerCalculator)
                .calculateDefendersPower(
                        new FightingArmy(500, 100, 200, 100),
                        context,
                        opponent,
                        context.game().getFields()[0][0]
                );
        doReturn(0.2).when(winnersArmyDestructionRatioCalculator)
                .calculateDestructionRatio(50000, 10000, gameBalanceService.getGameBalance());
        doReturn(new FightingArmy(0, 0, 0, 0)).when(attackersArmyAfterAttackCalculator).calculateAttackersArmyAfter(
                new FightingArmy(100, 20, 40, 0),
                false,
                0.2,
                player,
                gameBalanceService.getGameBalance()
        );
        doReturn(new FightingArmy(400, 80, 160, 80)).when(defendersArmyAfterAttackCalculator).calculateDefendersArmyAfter(
                new FightingArmy(500, 100 ,200, 100),
                false,
                0.2
        );

        var result = tested.executeBattleAgainstPlayer(attackingArmy, context, context.game().getFields()[0][0]);
        var expectedResult = new BattleResult(
                new FightingArmy(100, 20, 40, 0),
                new FightingArmy(500, 100, 200, 100),
                new FightingArmy(0, 0, 0, 0),
                new FightingArmy(400, 80, 160, 80),
                false,
                false
        );
        assertThat(expectedResult, sameBeanAs(result));
    }
}