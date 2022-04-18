package com.github.maciejmalewicz.Desert21.config.gameBalance;

import com.github.maciejmalewicz.Desert21.models.balance.buildings.FactoryConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.HomeBaseConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.RocketLauncherConfig;
import com.github.maciejmalewicz.Desert21.models.balance.buildings.TowerConfig;
import com.github.maciejmalewicz.Desert21.utils.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuildingsConfiguration {

    private final static String PATH = "balance/buildingsConfig.yml";

    @Bean
    public FactoryConfig factoriesConfiguration() {
        return BeanFactory.getBean(PATH, FactoryConfig.class);
    }

    @Bean("towerConfig")
    public TowerConfig towerConfiguration() {
        return BeanFactory.getBean(PATH, TowerConfig.class);
    }

    @Bean("homeBaseConfig")
    public HomeBaseConfig homeBaseConfiguration() {
        return BeanFactory.getBean(PATH, HomeBaseConfig.class);
    }

    @Bean
    public RocketLauncherConfig rocketLauncherConfiguration() {
        return BeanFactory.getBean(PATH, RocketLauncherConfig.class);
    }


}
