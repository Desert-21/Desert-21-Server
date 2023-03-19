package com.github.maciejmalewicz.Desert21.ai.parameters;

import com.github.maciejmalewicz.Desert21.domain.games.Army;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreezeUnitsActionParameters {
    private Location location;
    private Army maxArmyToFreeze;
}
