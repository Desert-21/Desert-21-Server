package com.github.maciejmalewicz.Desert21.dto.balance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.FactoryConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.HomeBaseConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.RocketLauncherConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.TowerConfig;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AllBuildingsBalanceDto(
        FactoryConfig factory,
        TowerConfig tower,
        HomeBaseConfig homeBase,
        RocketLauncherConfig rocketLauncher
) {
}
