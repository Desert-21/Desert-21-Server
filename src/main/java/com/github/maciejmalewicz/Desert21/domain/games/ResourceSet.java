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

    //todo: refactor constructors
    public static ResourceSet ofEachResourceAmount(int amount) {
        return new ResourceSet(amount, amount, amount);
    }

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

    public ResourceSet multiplyBy(double multiplier) {
        return new ResourceSet(
                (int) Math.round(metal * multiplier),
                (int) Math.round(buildingMaterials * multiplier),
                (int) Math.round(electricity * multiplier)
        );
    }

    public boolean isNonNegative() {
        return metal >= 0 && buildingMaterials >= 0 && electricity >= 0;
    }
}
