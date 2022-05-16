package com.github.maciejmalewicz.Desert21.models.balance;

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

    public T getAtLevel(int level) {
        return switch(level) {
            case 1 -> level1;
            case 2 -> level2;
            case 3 -> level3;
            case 4 -> level4;
            default -> null;
        };
    }
}
