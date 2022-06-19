package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class DefendersArmyAfterAttackCalculatorTest {

    @Autowired
    private DefendersArmyAfterAttackCalculator tested;

    @Autowired
    private GameBalanceService gameBalanceService;

    private Player attacker;

    @BeforeEach
    void setup() {
        attacker = new Player("AA",
                "macior123456",
                new ResourceSet(60, 60, 60));
    }

    @Test
    void calculateDefendersArmyAfterDefendersLost() {
        var defendersArmyAfter = tested.calculateDefendersArmyAfter(
                new FightingArmy(400, 100, 200, 50),
                true,
                0.5
        );
        assertThat(new FightingArmy(0, 0, 0, 0), sameBeanAs(defendersArmyAfter));
    }

    @Test
    void calculateDefendersArmyAfterDefendersWon() {
        var defendersArmyAfter = tested.calculateDefendersArmyAfter(
                new FightingArmy(400, 100, 200, 50),
                false,
                0.5
        );
        assertThat(new FightingArmy(200, 50, 100, 25), sameBeanAs(defendersArmyAfter));
    }
}