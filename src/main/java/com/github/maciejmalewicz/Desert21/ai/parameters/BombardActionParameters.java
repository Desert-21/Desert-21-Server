package com.github.maciejmalewicz.Desert21.ai.parameters;

import com.github.maciejmalewicz.Desert21.ai.helpers.LocatedCannons;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BombardActionParameters {
    private Location location;
    private List<LocatedCannons> fromCannons;

    public BombardActionParameters(Location location, LocatedCannons locatedCannons) {
        this.location = location;
        this.fromCannons = new ArrayList<>();
        this.fromCannons.add(locatedCannons);
    }
}
