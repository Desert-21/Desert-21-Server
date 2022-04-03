package com.github.maciejmalewicz.Desert21.config.gameBalance;

import com.github.maciejmalewicz.Desert21.misc.balance.GeneralConfig;
import com.github.maciejmalewicz.Desert21.utils.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {

    @Bean
    public GeneralConfig generalConfig() {
        return BeanFactory.getBean("balance/generalConfig.yml", GeneralConfig.class);
    }
}
