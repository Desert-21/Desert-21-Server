package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.combat;

import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WinnersArmyDestructionRatioCalculator {

    public double calculateDestructionRatio(int winnersPower, int losersPower, GameBalanceDto balance) {
        winnersPower++;
        losersPower++; // avoid division by 0
        var powersRatio = (double) losersPower / (double) winnersPower;
        var polynomial = balance.combat().general().getDestructionFunctionPolynomial();
        return applyDestructionPolynomialOnPowersRatio(polynomial, powersRatio);
    }

    private double applyDestructionPolynomialOnPowersRatio(List<Double> polynomial, double powersRatio) {
        double accumulator = 0;
        for (int i = 0; i < polynomial.size(); i++) {
            var index = polynomial.size() - 1 - i;
            var coefficient = polynomial.get(index);
            accumulator += coefficient * Math.pow(powersRatio, i);
        }
        return accumulator;
    }
}
