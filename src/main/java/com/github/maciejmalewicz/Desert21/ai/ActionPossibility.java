package com.github.maciejmalewicz.Desert21.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActionPossibility<Parameters> {
    private ActionPossibilityType actionPossibilityType;
    private Parameters parameters;
}
