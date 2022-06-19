package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.domain.games.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
class AgainstScarabsBattleExecutorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    private AgainstScarabsBattleExecutor tested;

    private ArmyPowerCalculator armyPowerCalculator;
    private WinnersArmyDestructionRatioCalculator winnersArmyDestructionRatioCalculator;
    private AttackersArmyAfterAttackCalculator attackersArmyAfterAttackCalculator;
    private ScarabsGenerator scarabsGenerator;
    private ScarabsPowerCalculator scarabsPowerCalculator;

    private Player player;
    private TurnExecutionContext context;

    void setupTested() {
        scarabsGenerator = mock(ScarabsGenerator.class);
        scarabsPowerCalculator = mock(ScarabsPowerCalculator.class);
        armyPowerCalculator = mock(ArmyPowerCalculator.class);
        winnersArmyDestructionRatioCalculator = mock(WinnersArmyDestructionRatioCalculator.class);
        attackersArmyAfterAttackCalculator = mock(AttackersArmyAfterAttackCalculator.class);
        tested = new AgainstScarabsBattleExecutor(
                scarabsGenerator,
                scarabsPowerCalculator,
                armyPowerCalculator,
                winnersArmyDestructionRatioCalculator,
                attackersArmyAfterAttackCalculator
        );
    }

    void setupContext() {
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
    }

    @BeforeEach
    void setup() throws NotAcceptableException {
        setupContext();
        setupTested();
    }

    @Test
    void executeBattleAgainstScarabsAttackerWinning() {
        var attackers = new Army(30, 10, 20);

        doReturn(100).when(scarabsGenerator).generateScarabs(context);
        doReturn(1000).when(scarabsPowerCalculator).calculateScarabsPower(100, context);
        doReturn(2000).when(armyPowerCalculator).calculateAttackersPower(
                new FightingArmy(30, 10, 20, 0),
                context
        );
        doReturn(0.5).when(winnersArmyDestructionRatioCalculator).calculateDestructionRatio(2000, 1000, gameBalanceService.getGameBalance());
        doReturn(new FightingArmy(15, 5, 10, 0)).when(attackersArmyAfterAttackCalculator).calculateAttackersArmyAfter(
                new FightingArmy(30, 10, 20, 0),
                true,
                0.5,
                player,
                gameBalanceService.getGameBalance()
        );

        var battleResult = tested.executeBattleAgainstScarabs(attackers, context);
        var expectedBattleResult = new BattleResult(
                new FightingArmy(30, 10, 20, 0),
                new FightingArmy(0, 0, 0, 100),
                new FightingArmy(15, 5, 10, 0),
                new FightingArmy(0, 0, 0, 0),
                true,
                true
        );
        assertThat(expectedBattleResult, sameBeanAs(battleResult));
    }

    @Test
    void executeBattleAgainstScarabsDefenderWinning() {
        var attackers = new Army(10, 2, 2);

        doReturn(200).when(scarabsGenerator).generateScarabs(context);
        doReturn(2000).when(scarabsPowerCalculator).calculateScarabsPower(200, context);
        doReturn(500).when(armyPowerCalculator).calculateAttackersPower(
                new FightingArmy(10, 2, 2, 0),
                context
        );
        doReturn(0.25).when(winnersArmyDestructionRatioCalculator).calculateDestructionRatio(2000, 500, gameBalanceService.getGameBalance());
        doReturn(new FightingArmy(0, 0, 0, 0)).when(attackersArmyAfterAttackCalculator).calculateAttackersArmyAfter(
                new FightingArmy(10, 2, 2, 0),
                false,
                0.25,
                player,
                gameBalanceService.getGameBalance()
        );

        var battleResult = tested.executeBattleAgainstScarabs(attackers, context);
        var expectedBattleResult = new BattleResult(
                new FightingArmy(10, 2, 2, 0),
                new FightingArmy(0, 0, 0, 200),
                new FightingArmy(0, 0, 0, 0),
                new FightingArmy(0, 0, 0, 150),
                false,
                true
        );
        assertThat(expectedBattleResult, sameBeanAs(battleResult));
    }
}