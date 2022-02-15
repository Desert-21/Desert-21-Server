package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FightingArmy extends Army {
    private int scarabs;
}
