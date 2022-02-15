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
}
