package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.FireRocketActionParameters;
import org.springframework.stereotype.Service;

@Service
public class FireRocketActionRatingEngine implements RatingEngine<FireRocketActionParameters> {
    @Override
    public double rateAction(FireRocketActionParameters fireRocketActionParameters) {
        return Math.random();
    }
}
