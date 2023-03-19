package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.FreezeUnitsActionParameters;
import org.springframework.stereotype.Service;

@Service
public class FreezeUnitsActionRatingEngine implements RatingEngine<FreezeUnitsActionParameters>{
    @Override
    public double rateAction(FreezeUnitsActionParameters freezeUnitsActionParameters) {
        return Math.random();
    }
}
