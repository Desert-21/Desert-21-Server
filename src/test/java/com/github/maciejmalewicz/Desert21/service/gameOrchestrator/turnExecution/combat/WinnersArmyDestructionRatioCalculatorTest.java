package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WinnersArmyDestructionRatioCalculatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    @Autowired
    private WinnersArmyDestructionRatioCalculator tested;

    @Test
    void calculateDestructionRatio() {
        // no damage
        // raw power ratio: 0 + 1 / 10 000 + 1 = 1 / 10 001 ~= 0
        // damage function: 0.4 * 0^2 + 0.6 * 0 = 0 + 0 = 0
        var ratioNoDamage = tested.calculateDestructionRatio(10000, 0, gameBalanceService.getGameBalance());
        assertEquals(0.0, ratioNoDamage, 0.01);

        // small
        // raw power ratio: 1000 + 1 / 10 000 + 1 = 1001 / 10 001 ~= 0.1
        // damage function: 0.4 * 0.1^2 + 0.6 * 0.1 = 0.004 + 0.06 = 0.064
        var ratioSmall = tested.calculateDestructionRatio(10000, 1000, gameBalanceService.getGameBalance());
        assertEquals(0.064, ratioSmall, 0.01);
        // middle

        // raw power ratio: 5000 + 1 / 10 000 + 1 = 5001 / 10 001 ~= 0.5
        // damage function: 0.4 * 0.5^2 + 0.6 * 0.5 = 0.1 + 0.3 = 0.4
        var ratioMiddle = tested.calculateDestructionRatio(10000, 5000, gameBalanceService.getGameBalance());
        assertEquals(0.4, ratioMiddle, 0.01);

        // high
        // raw power ratio: 8000 + 1 / 10 000 + 1 = 8001 / 10 001 ~= 0.8
        // damage function: 0.4 * 0.8^2 + 0.6 * 0.8 = 0.256 + 0.48 = 0.736
        var ratioBig = tested.calculateDestructionRatio(10000, 8000, gameBalanceService.getGameBalance());
        assertEquals(0.736, ratioBig, 0.01);

        // draw
        // raw power ratio: 10 000 + 1 / 10 000 + 1 = 10 001 / 10 001 = 1
        // damage function: 0.4 * 1^2 + 0.6 * 1 = 0.4 + 0.6 = 1
        var ratioDraw = tested.calculateDestructionRatio(10000, 10000, gameBalanceService.getGameBalance());
        assertEquals(1, ratioDraw, 0.01);

    }
}