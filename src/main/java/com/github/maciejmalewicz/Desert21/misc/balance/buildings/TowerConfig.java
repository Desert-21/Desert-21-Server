package com.github.maciejmalewicz.Desert21.misc.balance.buildings;

import com.github.maciejmalewicz.Desert21.misc.balance.LeveledValue;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.BuildingConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TowerConfig extends BuildingConfig {
    private LeveledValue<Integer> baseProtection;
    private LeveledValue<Double> unitBonus;
}
