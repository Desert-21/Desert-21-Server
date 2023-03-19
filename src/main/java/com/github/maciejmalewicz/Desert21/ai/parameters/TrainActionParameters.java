package com.github.maciejmalewicz.Desert21.ai.parameters;

import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.TrainingMode;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainActionParameters {
    private int metalCost;
    private Location location;
    private UnitType unitType;
    private TrainingMode trainingMode;
}
