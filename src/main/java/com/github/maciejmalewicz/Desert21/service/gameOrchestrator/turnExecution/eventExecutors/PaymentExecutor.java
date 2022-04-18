package com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventExecutors;

import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.turnExecution.TurnExecutionContext;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.PaymentEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentExecutor implements EventExecutor<PaymentEvent> {

    @Override
    public TurnExecutionContext execute(List<PaymentEvent> events, TurnExecutionContext context) throws NotAcceptableException {
        var currentResources = context.player().getResources();
        var toPay = events.stream()
                .map(PaymentEvent::getResourceSet)
                .reduce(ResourceSet::add)
                .orElse(new ResourceSet(0, 0, 0));
        var afterPayment = currentResources.subtract(toPay);
        context.player().setResources(afterPayment);
        return context;
    }
}
