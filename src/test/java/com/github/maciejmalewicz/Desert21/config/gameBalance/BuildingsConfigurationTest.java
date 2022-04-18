package com.github.maciejmalewicz.Desert21.config.gameBalance;

import com.github.maciejmalewicz.Desert21.models.balance.buildings.FactoryConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.HomeBaseConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.RocketLauncherConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.TowerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuildingsConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void factoriesConfiguration() {
        var bean = applicationContext.getBean(FactoryConfig.class);
        assertNotNull(bean);
    }

    @Test
    void towerConfiguration() {
        var bean = applicationContext.getBean("towerConfig", TowerConfig.class);
        assertNotNull(bean);
    }

    @Test
    void homeBaseConfiguration() {
        var bean = applicationContext.getBean("homeBaseConfig", HomeBaseConfig.class);
        assertNotNull(bean);
    }

    @Test
    void rocketLauncherConfiguration() {
        var bean = applicationContext.getBean(RocketLauncherConfig.class);
        assertNotNull(bean);
    }
}