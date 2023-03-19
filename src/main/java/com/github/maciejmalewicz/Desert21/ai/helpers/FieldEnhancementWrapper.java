package com.github.maciejmalewicz.Desert21.ai.helpers;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import lombok.Data;

@Data
public class FieldEnhancementWrapper extends Field {

    private boolean isAlreadyTrainingHere;
    private boolean isAlreadyBuildingHere;
    private boolean isAlreadyUpgradingHere;

    public FieldEnhancementWrapper(Field field) {
        super(field.getBuilding(), field.getOwnerId());
        this.army = field.getArmy();
        this.isAlreadyTrainingHere = false;
        this.isAlreadyBuildingHere = false;
        this.isAlreadyUpgradingHere = false;
    }
}
