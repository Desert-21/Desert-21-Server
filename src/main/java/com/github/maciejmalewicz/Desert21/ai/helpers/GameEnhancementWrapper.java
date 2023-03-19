package com.github.maciejmalewicz.Desert21.ai.helpers;

import com.github.maciejmalewicz.Desert21.config.gameBalance.lab.LabUpgrade;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameEnhancementWrapper extends Game {

    private ResourceSet lockedResources;
    private boolean isRocketAlreadyFired;
    private List<LabUpgrade> currentTurnUpgrades;

    public GameEnhancementWrapper(Game game) {
        super(game.getPlayers(), game.getStateManager());
        fields = processFields(game.getFields());
        lockedResources = new ResourceSet(0, 0, 0);
        isRocketAlreadyFired = false;
        currentTurnUpgrades = new ArrayList<>();
    }

    private FieldEnhancementWrapper[][] processFields(Field[][] fields) {
        var processed = new FieldEnhancementWrapper[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            processed[i] = new FieldEnhancementWrapper[fields[i].length];
            for (int j = 0; j < fields[i].length; j++) {
                processed[i][j] = new FieldEnhancementWrapper(fields[i][j]);
            }
        }
        return processed;
    }
}
