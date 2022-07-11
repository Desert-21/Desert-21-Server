package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BombardingEvent extends GameEvent {

    private Location target;
    private int cannons;
}
