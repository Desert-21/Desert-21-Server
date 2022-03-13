package com.github.maciejmalewicz.Desert21.misc.balance.buildings;

import com.github.maciejmalewicz.Desert21.misc.balance.buildings.BuildingConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RocketLauncherConfig extends BuildingConfig {
    private int firstUseCost;
    private int nextUseCostIncrease;
}
