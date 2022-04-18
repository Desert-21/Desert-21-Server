package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventExecutorTest {

    @Test
    void getExecutableClass() {
        var eventExecutor = new EventExecutor<AnyClass>() {
            @Override
            public TurnExecutionContext execute(List<AnyClass> events, TurnExecutionContext context) throws NotAcceptableException {
                return null;
            }
        };
        var clazz = eventExecutor.getExecutableClass();
        assertEquals(AnyClass.class, clazz);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static final class AnyClass extends GameEvent {
        int property;
    }
}