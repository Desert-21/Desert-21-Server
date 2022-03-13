package com.github.maciejmalewicz.Desert21.misc.balance.buildings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HomeBaseConfig extends TowerConfig {
    private int production;
}
