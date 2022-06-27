package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import com.github.maciejmalewicz.Desert21.models.balance.lab.*;
import com.github.maciejmalewicz.Desert21.utils.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LabConfiguration {

    private final static String PATH = "balance/upgradesConfig.yml";

    @Bean
    public CombatBranchConfig combatBranchConfig() {
        var config = BeanFactory.getBean(PATH, CombatBranchBalanceConfig.class);
        var costConfig = BeanFactory.getBean(PATH, CombatBranchCostConfig.class);
        return new CombatBranchConfig(config, costConfig);
    }

    @Bean
    public ControlBranchConfig controlBranchConfig() {
        var config = BeanFactory.getBean(PATH, ControlBranchBalanceConfig.class);
        var costConfig = BeanFactory.getBean(PATH, ControlBranchCostConfig.class);
        return new ControlBranchConfig(config, costConfig);
    }

    @Bean
    public ProductionBranchConfig productionBranchConfig() {
        var config = BeanFactory.getBean(PATH, ProductionBranchBalanceConfig.class);
        var costConfig = BeanFactory.getBean(PATH, ProductionBranchCostConfig.class);
        return new ProductionBranchConfig(config, costConfig);
    }


}
