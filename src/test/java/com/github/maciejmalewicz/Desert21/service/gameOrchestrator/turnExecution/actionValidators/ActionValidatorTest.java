package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ActionValidatable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionValidatorTest {

    @Test
    void getValidatableClass() {
        var actionValidator = new ActionValidator<AnyClass>() {
            @Override
            public boolean validate(List<AnyClass> validatables, TurnExecutionContext context) {
                return false;
            }
        };
        var clazz = actionValidator.getValidatableClass();
        assertEquals(AnyClass.class, clazz);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static final class AnyClass implements ActionValidatable {
        int property;
    }
}