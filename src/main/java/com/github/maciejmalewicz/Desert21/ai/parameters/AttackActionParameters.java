package com.github.maciejmalewicz.Desert21.ai.parameters;

import com.github.maciejmalewicz.Desert21.ai.helpers.LocatedArmy;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AttackActionParameters {
    private Location location;
    private List<LocatedArmy> fromArmies;

    public AttackActionParameters(Location location, LocatedArmy locatedArmy) {
        this.location = location;
        this.fromArmies = new ArrayList<>();
        this.fromArmies.add(locatedArmy);
    }
}
