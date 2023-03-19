package com.github.maciejmalewicz.Desert21.ai.parameters;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpgradeBuildingActionParameters {
    private int buildingMaterialsCost;
    private Location location;
    private BuildingType buildingType;
    private int level;

    public UpgradeBuildingActionParameters(Location location, BuildingType buildingType, int buildingMaterialsCost, int level) {
        this.buildingMaterialsCost = buildingMaterialsCost;
        this.location = location;
        this.buildingType = buildingType;
        this.level = level;
    }
}
