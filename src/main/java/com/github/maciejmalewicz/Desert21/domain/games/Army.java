package com.github.maciejmalewicz.Desert21.domain.games;

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

    public boolean isEmpty() {
        return droids == 0 && tanks == 0 && cannons == 0;
    }
}
