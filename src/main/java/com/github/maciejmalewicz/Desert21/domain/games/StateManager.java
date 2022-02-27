package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateManager {

    private GameState gameState;
    private Date timeout;
    private String currentPlayerId;

    private String currentStateTimeoutId;
}
