package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.TrainActionParameters;
import org.springframework.stereotype.Service;

@Service
public class TrainActionRatingEngine implements RatingEngine<TrainActionParameters> {
    @Override
    public double rateAction(TrainActionParameters trainActionParameters) {
        return Math.random();
    }
}
