package com.github.maciejmalewicz.Desert21.ai.parameters;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabActionParameters {
    private int electricityCost;
    private LabUpgrade labUpgrade;
}
