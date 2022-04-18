package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidators;

import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.actionValidatables.ActionValidatable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public interface ActionValidator <T extends ActionValidatable> {
    boolean validate(List<T> validatables, TurnExecutionContext context);

    default Class<T> getValidatableClass() {
        var claz = this.getClass();
        var parameterizedType = (ParameterizedType) claz.getGenericInterfaces()[0];
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return (Class<T>) typeArguments[0];
    };
}
