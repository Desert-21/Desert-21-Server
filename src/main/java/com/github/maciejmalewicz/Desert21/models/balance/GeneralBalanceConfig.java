package com.github.maciejmalewicz.Desert21.models.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeneralBalanceConfig {

    private int startingResources;

    private int fogOfWar1;
    private int fogOfWar2;

    private int productionPerField;

    private double rocketStrikeDamage;
    private int baseRocketStrikePrice;
    private int rocketStrikePricePerUsage;
}
