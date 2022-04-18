package com.github.maciejmalewicz.Desert21.domain.games;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Building {
    private BuildingType type;
    private int level;

    public Building(BuildingType type) {
        this.type = type;
        this.level = 1;
    }

    public Building(BuildingType type, int level) {
        this.type = type;
        this.level = level;
    }
}
