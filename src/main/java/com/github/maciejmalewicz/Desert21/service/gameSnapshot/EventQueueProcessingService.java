package com.github.maciejmalewicz.Desert21.service.gameSnapshot;

import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.dto.game.EventDto;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.*;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;

@Service
public class EventQueueProcessingService {

    public List<EventDto> processEventQueue(List<GameEvent> eventQueue, Player player, Field[][] allFields) {
        return eventQueue.stream()
                .filter(event -> shouldShowEventToPlayer(event, player, allFields))
                .map(e -> new EventDto(getEventLabel(e), e))
                .toList();
    }

    private boolean shouldShowEventToPlayer(GameEvent gameEvent, Player player, Field[][] allFields) {
        if (gameEvent.getClass() != ArmyTrainingEvent.class && gameEvent.getClass() != BuildBuildingEvent.class) {
            return true;
        }
        var locatableEvent = (LocatableEvent) gameEvent;
        try {
            var field = BoardUtils.fieldAtLocation(allFields, locatableEvent.getLocation());
            return player.getId().equals(field.getOwnerId());
        } catch (NotAcceptableException e) {
            return false;
        }
    }

    private String getEventLabel(GameEvent event) {
        if (event instanceof BuildingUpgradeEvent) {
            return UPGRADE_EVENT;
        }
        if (event instanceof ArmyTrainingEvent) {
            return TRAINING_EVENT;
        }
        if (event instanceof BuildBuildingEvent) {
            return BUILD_EVENT;
        }
        return "";
    }
}
