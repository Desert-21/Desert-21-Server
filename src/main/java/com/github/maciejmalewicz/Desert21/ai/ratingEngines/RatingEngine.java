package com.github.maciejmalewicz.Desert21.ai.ratingEngines;

public interface RatingEngine<ActionParameters> {
    double rateAction(ActionParameters parameters);
}
