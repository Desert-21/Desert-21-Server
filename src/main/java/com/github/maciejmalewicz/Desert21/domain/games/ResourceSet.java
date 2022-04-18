package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourceSet {

    private int metal;
    private int buildingMaterials;
    private int electricity;

    public ResourceSet add(ResourceSet other) {
        return new ResourceSet(
                metal + other.getMetal(),
                buildingMaterials + other.getBuildingMaterials(),
                electricity + other.getElectricity()
        );
    }

    public ResourceSet subtract(ResourceSet other) {
        return new ResourceSet(
                metal - other.getMetal(),
                buildingMaterials - other.getBuildingMaterials(),
                electricity - other.getElectricity()
        );
    }

    public boolean isNonNegative() {
        return metal >= 0 && buildingMaterials >= 0 && electricity >= 0;
    }
}
