package com.github.maciejmalewicz.Desert21.ai.core;

import com.github.maciejmalewicz.Desert21.ai.actionsPickers.ArmyActionsPicker;
import com.github.maciejmalewicz.Desert21.ai.actionsPickers.BuildingMaterialsActionsPicker;
import com.github.maciejmalewicz.Desert21.ai.actionsPickers.ElectricityActionsPicker;
import com.github.maciejmalewicz.Desert21.ai.actionsPickers.MetalActionsPicker;
import com.github.maciejmalewicz.Desert21.ai.helpers.AiTurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actions.Action;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
public class AiTurnActionsGenerator {

    private final MetalActionsPicker metalActionsPicker;
    private final BuildingMaterialsActionsPicker buildingMaterialsActionsPicker;
    private final ElectricityActionsPicker electricityActionsPicker;
    private final ArmyActionsPicker armyActionsPicker;

    public AiTurnActionsGenerator(MetalActionsPicker metalActionsPicker, BuildingMaterialsActionsPicker buildingMaterialsActionsPicker, ElectricityActionsPicker electricityActionsPicker, ArmyActionsPicker armyActionsPicker) {
        this.metalActionsPicker = metalActionsPicker;
        this.buildingMaterialsActionsPicker = buildingMaterialsActionsPicker;
        this.electricityActionsPicker = electricityActionsPicker;
        this.armyActionsPicker = armyActionsPicker;
    }

    public List<Action> getFullActionChoice(AiTurnExecutionContext context) {
        return Stream.of(
                buildingMaterialsActionsPicker,
                metalActionsPicker,
                electricityActionsPicker,
                armyActionsPicker
        )
                .map(a -> a.getActions(context))
                .flatMap(Collection::stream)
                .toList();
    }
}
