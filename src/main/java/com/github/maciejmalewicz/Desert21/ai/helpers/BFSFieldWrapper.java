package com.github.maciejmalewicz.Desert21.ai.helpers;

import com.github.maciejmalewicz.Desert21.ai.ActionPossibility;
import com.github.maciejmalewicz.Desert21.ai.parameters.AttackActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.BombardActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.FreezeUnitsActionParameters;
import com.github.maciejmalewicz.Desert21.ai.parameters.MoveUnitsActionParameters;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.models.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BFSFieldWrapper {
    private Field field;
    private Location location;
    private ActionPossibility<MoveUnitsActionParameters> moveUnitsActionPossibility;
    private ActionPossibility<AttackActionParameters> attackActionPossibility;
    private ActionPossibility<BombardActionParameters> bombardActionPossibility;
    private ActionPossibility<FreezeUnitsActionParameters> freezeUnitsActionPossibility;
    private boolean alreadyVisited;

    public BFSFieldWrapper(Field field, Location location) {
        this.field = field;
        this.location = location;
        this.moveUnitsActionPossibility = null;
        this.attackActionPossibility = null;
        this.bombardActionPossibility = null;
        this.freezeUnitsActionPossibility = null;
        this.alreadyVisited = false;
    }
}
