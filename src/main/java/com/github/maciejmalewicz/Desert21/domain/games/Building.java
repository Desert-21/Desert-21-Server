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

    public boolean isFactory() {
        return type == BuildingType.METAL_FACTORY
                || type == BuildingType.BUILDING_MATERIALS_FACTORY
                || type == BuildingType.ELECTRICITY_FACTORY;
    }

}
