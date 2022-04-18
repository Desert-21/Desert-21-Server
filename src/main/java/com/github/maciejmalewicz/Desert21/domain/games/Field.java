package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Field {

    private Building building;
    private String ownerId;
    private Army army = new Army();

    public Field(Building building) {
        this.building = building;
        this.ownerId = null;
    }

    public Field(Building building, String ownerId) {
        this.building = building;
        this.ownerId = ownerId;
    }
}
