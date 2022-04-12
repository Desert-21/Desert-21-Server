package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.dto.balance.AllBuildingsBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.AllCombatBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.misc.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.FactoryConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.HomeBaseConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.RocketLauncherConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.TowerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GameBalanceService {

    @Autowired
    private FactoryConfig factoryConfig;
    @Autowired
    @Qualifier("towerConfig")
    private TowerConfig towerConfig;
    @Autowired
    @Qualifier("homeBaseConfig")
    private HomeBaseConfig homeBaseConfig;

    @Autowired
    private RocketLauncherConfig rocketLauncherConfig;

    @Autowired
    @Qualifier("droidsConfig")
    private CombatUnitConfig droidsConfig;

    @Autowired
    @Qualifier("tanksConfig")
    private CombatUnitConfig tanksConfig;

    @Autowired
    @Qualifier("cannonsConfig")
    private CombatUnitConfig cannonsConfig;

    public GameBalanceDto getGameBalance() {
        return new GameBalanceDto(
                new AllBuildingsBalanceDto(
                        factoryConfig,
                        towerConfig,
                        homeBaseConfig,
                        rocketLauncherConfig
                ),
                new AllCombatBalanceDto(
                        droidsConfig,
                        tanksConfig,
                        cannonsConfig
                )
        );
    }
}
