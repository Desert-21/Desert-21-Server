package com.github.maciejmalewicz.Desert21.domain.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Army {
    private int droids;
    private int tanks;
    private int cannons;

    @JsonIgnore
    public boolean isEmpty() {
        return droids == 0 && tanks == 0 && cannons == 0;
    }

    public Army combineWith(Army army) {
        return new Army(droids + army.getDroids(), tanks + army.getTanks(), cannons + army.getCannons());
    }
}
