package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import com.github.maciejmalewicz.Desert21.ai.parameters.AttackActionParameters;
import org.springframework.stereotype.Service;

@Service
public class AttackActionRatingEngine implements RatingEngine<AttackActionParameters> {
    @Override
    public double rateAction(AttackActionParameters attackActionParameters) {
        return Math.random();
    }
}
