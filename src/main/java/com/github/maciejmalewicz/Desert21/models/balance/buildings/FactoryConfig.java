package com.github.maciejmalewicz.Desert21.models.balance.buildings;

import com.github.maciejmalewicz.Desert21.models.balance.LeveledValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FactoryConfig extends BuildingConfig {
    private LeveledValue<Integer> production;
}
