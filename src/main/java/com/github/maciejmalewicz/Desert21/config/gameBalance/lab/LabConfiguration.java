package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import com.github.maciejmalewicz.Desert21.models.balance.lab.CombatBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.ControlBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.models.balance.lab.ProductionBranchBalanceConfig;
import com.github.maciejmalewicz.Desert21.utils.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LabConfiguration {

    private final static String PATH = "balance/upgradesConfig.yml";

    @Bean
    public CombatBranchConfig combatBranchConfig() {
        var config = BeanFactory.getBean(PATH, CombatBranchBalanceConfig.class);
        return new CombatBranchConfig(config);
    }

    @Bean
    public ControlBranchConfig controlBranchConfig() {
        var config = BeanFactory.getBean(PATH, ControlBranchBalanceConfig.class);
        return new ControlBranchConfig(config);
    }

    @Bean
    public ProductionBranchConfig productionBranchConfig() {
        var config = BeanFactory.getBean(PATH, ProductionBranchBalanceConfig.class);
        return new ProductionBranchConfig(config);
    }
}
