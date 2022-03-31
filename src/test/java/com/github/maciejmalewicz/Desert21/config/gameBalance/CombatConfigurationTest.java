package com.github.maciejmalewicz.Desert21.config.gameBalance;

import com.github.maciejmalewicz.Desert21.misc.balance.CombatUnitConfig;
import com.github.maciejmalewicz.Desert21.misc.balance.buildings.FactoryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CombatConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void droidsConfig() {
        var bean = applicationContext.getBean("droidsConfig", CombatUnitConfig.class);
        assertNotNull(bean);
    }

    @Test
    void tanksConfig() {
        var bean = applicationContext.getBean("droidsConfig", CombatUnitConfig.class);
        assertNotNull(bean);
    }

    @Test
    void cannonsConfig() {
        var bean = applicationContext.getBean("tanksConfig", CombatUnitConfig.class);
        assertNotNull(bean);
    }
}