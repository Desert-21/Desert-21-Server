package com.github.maciejmalewicz.Desert21.misc.balance.buildings;

import com.github.maciejmalewicz.Desert21.misc.balance.LeveledValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildingConfig {

    private LeveledValue<Integer> cost;
}
