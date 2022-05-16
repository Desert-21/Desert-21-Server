package com.github.maciejmalewicz.Desert21.service;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.CombatBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.ControlBranchConfig;
import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.ProductionBranchConfig;
import com.github.maciejmalewicz.Desert21.dto.balance.AllBuildingsBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.AllCombatBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.AllUpgradesBalanceDto;
import com.github.maciejmalewicz.Desert21.dto.balance.GameBalanceDto;
import com.github.maciejmalewicz.Desert21.models.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.models.balance.GeneralBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.FactoryConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.HomeBaseConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.RocketLauncherConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.TowerConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.CombatBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.ControlBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.ProductionBranchBalanceConfig;
import org.checkerframework.checker.units.qual.A;
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

    @Autowired
    private CombatBranchConfig combatBranchConfig;

    @Autowired
    private ControlBranchConfig controlBranchConfig;

    @Autowired
    private ProductionBranchConfig productionBranchConfig;

    @Autowired
    private GeneralBalanceConfig generalBalanceConfig;

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
                ),
                new AllUpgradesBalanceDto(
                        combatBranchConfig,
                        controlBranchConfig,
                        productionBranchConfig
                ),
                generalBalanceConfig
        );
    }
}
