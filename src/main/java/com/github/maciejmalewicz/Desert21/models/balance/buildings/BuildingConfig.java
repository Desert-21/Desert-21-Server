package com.github.maciejmalewicz.Desert21.models.balance.buildings;

import com.github.maciejmalewicz.Desert21.models.balance.LeveledValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildingConfig {

    private LeveledValue<Integer> cost;

    public int costAtLevel(int level) {
        return switch (level) {
            case 1 -> cost.getLevel1();
            case 2 -> cost.getLevel2();
            case 3 -> cost.getLevel3();
            case 4 -> cost.getLevel4();
            default -> -1;
        };
    }
}
