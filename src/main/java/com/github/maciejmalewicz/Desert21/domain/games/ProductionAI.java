package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.Data;

@Data
public class ProductionAI {
    private boolean isActivated;
    private int currentProduction;

    public ProductionAI() {
        this.isActivated = false;
        this.currentProduction = 0;
    }
}
