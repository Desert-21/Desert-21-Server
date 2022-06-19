package com.github.maciejmalewicz.Desert21.models.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeneralCombatConfig {
    private List<Double> destructionFunctionPolynomial;
}
