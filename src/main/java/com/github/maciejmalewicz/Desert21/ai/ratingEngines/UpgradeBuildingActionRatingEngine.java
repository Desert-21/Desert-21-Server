package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.UpgradeBuildingActionParameters;
import org.springframework.stereotype.Service;

@Service
public class UpgradeBuildingActionRatingEngine implements RatingEngine<UpgradeBuildingActionParameters> {
    @Override
    public double rateAction(UpgradeBuildingActionParameters upgradeBuildingActionParameters) {
        return Math.random();
    }
}
