package com.github.maciejmalewicz.Desert21.service.gameSnapshot;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.domain.games.ResourceSet;
import com.github.maciejmalewicz.Desert21.dto.game.EventDto;
import com.github.maciejmalewicz.Desert21.models.BuildingType;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildBuildingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.misc.UnitType;
import com.github.maciejmalewicz.Desert21.utils.BoardUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.maciejmalewicz.Desert21.config.Constants.BUILD_EVENT;
import static com.github.maciejmalewicz.Desert21.config.Constants.TRAINING_EVENT;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventQueueProcessingServiceTest {

    private EventQueueProcessingService tested = new EventQueueProcessingService();

    @Test
    void processEventQueue() {
        List<GameEvent> events = List.of(
                new BuildBuildingEvent(new Location(0, 0), BuildingType.TOWER),
                new BuildBuildingEvent(new Location(1, 1), BuildingType.ELECTRICITY_FACTORY),
                new ArmyTrainingEvent(2, new Location(0, 1), UnitType.TANK, 4),
                new ArmyTrainingEvent(2, new Location(0, 2), UnitType.TANK, 4)
        );
        var player = new Player("AA", "macior123456", new ResourceSet(60, 60, 60));
        var fields = BoardUtils.generateEmptyPlain(7);
        fields[0][0] = new Field(new Building(BuildingType.EMPTY_FIELD), "AA");
        fields[1][1] = new Field(new Building(BuildingType.EMPTY_FIELD), "BB");
        fields[0][1] = new Field(new Building(BuildingType.TOWER), "AA");
        fields[0][2] = new Field(new Building(BuildingType.TOWER), "BB");
        var processed = tested.processEventQueue(events, player, fields);
        var expectedQueue = List.of(
                new EventDto(BUILD_EVENT, new BuildBuildingEvent(new Location(0, 0), BuildingType.TOWER)),
                new EventDto(TRAINING_EVENT, new ArmyTrainingEvent(2, new Location(0, 1), UnitType.TANK, 4))
        );
        assertThat(expectedQueue, sameBeanAs(processed));
    }
}