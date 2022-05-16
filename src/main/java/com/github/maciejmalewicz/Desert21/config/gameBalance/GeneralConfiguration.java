package com.github.maciejmalewicz.Desert21.config.gameBalance;

import com.github.maciejmalewicz.Desert21.models.balance.GeneralBalanceConfig;
import com.github.maciejmalewicz.Desert21.utils.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {

    @Bean
    public GeneralBalanceConfig generalConfig() {
        return BeanFactory.getBean("balance/generalConfig.yml", GeneralBalanceConfig.class);
    }
}
