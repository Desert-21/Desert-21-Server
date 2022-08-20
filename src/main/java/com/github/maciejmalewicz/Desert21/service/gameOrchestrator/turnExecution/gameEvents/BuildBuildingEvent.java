package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents;

import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.mongodb.internal.VisibleForTesting;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.TurnsConstants.END_OF_NEXT_TURN;

@Data
@NoArgsConstructor
public class BuildBuildingEvent extends GameEvent implements LocatableEvent {

    private Location location;
    private BuildingType buildingType;

    public BuildBuildingEvent(Location location, BuildingType buildingType) {
        super(END_OF_NEXT_TURN);
        this.location = location;
        this.buildingType = buildingType;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    public BuildBuildingEvent(int turnsToExecute, Location location, BuildingType buildingType) {
        super(turnsToExecute);
        this.location = location;
        this.buildingType = buildingType;
    }
}
