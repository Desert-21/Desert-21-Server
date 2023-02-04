package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.service.GameBalanceService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.costCalculators.RocketCostCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RocketCostCalculatorTest {

    @Autowired
    private GameBalanceService gameBalanceService;

    @Test
    void calculateRocketCostBasic() {
        var fields = List.of(
                new Field(new Building(BuildingType.HOME_BASE), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA")
        );
        var cost = RocketCostCalculator.calculateRocketCost(
                gameBalanceService.getGameBalance(),
                new Player(0, false),
                fields
        );
        assertEquals(300, cost);
    }

    @Test
    void calculateRocketCostTwoLaunchersOneStrikePassed() {
        var fields = List.of(
                new Field(new Building(BuildingType.HOME_BASE), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA")
        );
        var cost = RocketCostCalculator.calculateRocketCost(
                gameBalanceService.getGameBalance(),
                new Player(1, false),
                fields
        );
        assertEquals(250, cost);
    }

    @Test
    void calculateRocketCostThreeLaunchersFiveStrikesPassed() {
        var fields = List.of(
                new Field(new Building(BuildingType.HOME_BASE), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA")
        );
        var cost = RocketCostCalculator.calculateRocketCost(
                gameBalanceService.getGameBalance(),
                new Player(5, false),
                fields
        );
        assertEquals(325, cost);
    }

    @Test
    void calculateRocketCostSuperSonicNextRocketFree() {
        var fields = List.of(
                new Field(new Building(BuildingType.HOME_BASE), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA"),
                new Field(new Building(BuildingType.ROCKET_LAUNCHER), "AA")
        );
        var cost = RocketCostCalculator.calculateRocketCost(
                gameBalanceService.getGameBalance(),
                new Player(5, true),
                fields
        );
        assertEquals(0, cost);
    }
}