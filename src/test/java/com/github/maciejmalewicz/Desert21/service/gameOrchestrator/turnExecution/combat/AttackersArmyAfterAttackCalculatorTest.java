package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
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
class AttackersArmyAfterAttackCalculatorTest {

    @Autowired
    private AttackersArmyAfterAttackCalculator tested;

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
    void calculateAttackersArmyAfterAttackersLost() {
        var attackersAfter = tested.calculateAttackersArmyAfter(
                new FightingArmy(400, 100, 200, 0),
                false,
                0.5,
                attacker,
                gameBalanceService.getGameBalance()
        );
        assertThat(new FightingArmy(0, 0, 0, 0), sameBeanAs(attackersAfter));
    }

    @Test
    void calculateAttackersArmyAfterAttackersWonWithoutReusableParts() {
        var attackersAfter = tested.calculateAttackersArmyAfter(
                new FightingArmy(400, 100, 200, 0),
                true,
                0.5,
                attacker,
                gameBalanceService.getGameBalance()
        );
        assertThat(new FightingArmy(200, 50, 100, 0), sameBeanAs(attackersAfter));
    }

    @Test
    void calculateAttackersArmyAfterAttackersWonWithReusableParts() {
        attacker.getOwnedUpgrades().add(LabUpgrade.REUSABLE_PARTS);
        var attackersAfter = tested.calculateAttackersArmyAfter(
                new FightingArmy(400, 100, 200, 0),
                true,
                0.5,
                attacker,
                gameBalanceService.getGameBalance()
        );
        assertThat(new FightingArmy(280, 70, 140, 0), sameBeanAs(attackersAfter));
    }
}