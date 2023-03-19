package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

import org.springframework.stereotype.Service;

@Service
public class BombardActionRatingEngine implements RatingEngine<BombardActionRatingEngine> {
    @Override
    public double rateAction(BombardActionRatingEngine bombardActionRatingEngine) {
        return Math.random();
    }
}
