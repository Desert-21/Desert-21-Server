package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.LabActionParameters;
import org.springframework.stereotype.Service;

@Service
public class LabActionRatingEngine implements RatingEngine<LabActionParameters> {
    @Override
    public double rateAction(LabActionParameters labActionParameters) {
        return Math.random();
    }
}
