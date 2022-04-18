package com.github.maciejmalewicz.Desert21.models.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CombatUnitConfig {
    private int cost;
    private int power;
    private int turnsToTrain;
    private int fieldsTraveledPerTurn;
    private int smallProduction;
    private int mediumProduction;
    private int massProduction;
}
