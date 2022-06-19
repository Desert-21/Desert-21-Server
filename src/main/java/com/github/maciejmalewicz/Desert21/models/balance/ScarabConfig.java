package com.github.maciejmalewicz.Desert21.models.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScarabConfig {
    private int power;
    private int baseGeneration;
    private int additionalGenerationPerTurn;
    private double generationBias;
}
