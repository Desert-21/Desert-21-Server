package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.BuildBuildingActionParameters;
import org.springframework.stereotype.Service;

@Service
public class BuildBuildingActionRatingEngine implements RatingEngine<BuildBuildingActionParameters>{
    @Override
    public double rateAction(BuildBuildingActionParameters buildBuildingActionParameters) {
        return Math.random();
    }
}
