package com.github.maciejmalewicz.Desert21.misc.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeveledValue <T> {

    private T level1;
    private T level2;
    private T level3;
    private T level4;
}
