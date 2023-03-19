package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import org.springframework.stereotype.Service;

@Service
public class WaitActionRatingEngine implements RatingEngine<Object> {
    @Override
    public double rateAction(Object o) {
        return Math.random();
    }
}
