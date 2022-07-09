package com.github.maciejmalewicz.Desert21.service.gameSnapshot;

import com.github.maciejmalewicz.Desert21.domain.games.Building;
import com.github.maciejmalewicz.Desert21.domain.games.Field;
import com.github.maciejmalewicz.Desert21.domain.games.Player;
import com.github.maciejmalewicz.Desert21.domain.games.StateManager;
import com.github.maciejmalewicz.Desert21.dto.game.*;
import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.models.Location;
import com.github.maciejmalewicz.Desert21.service.GamePlayerService;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.ArmyTrainingEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.BuildingUpgradeEvent;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.maciejmalewicz.Desert21.config.Constants.TRAINING_EVENT;
import static com.github.maciejmalewicz.Desert21.config.Constants.UPGRADE_EVENT;

@Service
public class GameSnapshotService {

    private final GamePlayerService gamePlayerService;
    private final ArmySnapshotProcessingService armySnapshotProcessingService;

    public GameSnapshotService(GamePlayerService gamePlayerService, ArmySnapshotProcessingService armySnapshotProcessingService) {
        this.gamePlayerService = gamePlayerService;
        this.armySnapshotProcessingService = armySnapshotProcessingService;
    }

    public GameDto snapshotGame(String gameId, Authentication authentication) throws NotAcceptableException {
        var gamePlayer = gamePlayerService.getGamePlayerData(gameId, authentication);
        var game = gamePlayer.game();
        var player = gamePlayer.player();
        return new GameDto(
                gameId,
                processPlayers(game.getPlayers()),
                processFields(game.getFields(), player),
                processStateManager(game.getStateManager()),
                processEventQueue(game.getEventQueue())
        );
    }

    private List<PlayerDto> processPlayers(List<Player> players) {
        return players.stream().map(p -> new PlayerDto(
                p.getId(),
                p.getNickname(),
                p.getResources(),
                p.getOwnedUpgrades(),
                p.getRocketStrikesDone()
        )).collect(Collectors.toList());
    }

    private FieldDto[][] processFields(Field[][] fields, Player player) {
        var fieldDtos = new FieldDto[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            fieldDtos[i] = new FieldDto[fields[i].length];
            for (int j = 0; j < fields[i].length; j++) {
                fieldDtos[i][j] = processField(fields[i][j], player, fields, new Location(i, j));
            }
        }
        return fieldDtos;
    }

    private FieldDto processField(Field field, Player player, Field[][] allFields, Location location) {
        return new FieldDto(
                processBuilding(field.getBuilding()),
                field.getOwnerId(),
                armySnapshotProcessingService.snapshotArmy(player, allFields, location)
        );
    }

    private BuildingDto processBuilding(Building building) {
        return new BuildingDto(
                building.getType(),
                building.getLevel()
        );
    }

    private StateManagerDto processStateManager(StateManager stateManager) {
        return new StateManagerDto(
                stateManager.getGameState(),
                stateManager.getTimeout(),
                stateManager.getCurrentPlayerId(),
                stateManager.getTurnCounter()
        );
    }

    private List<EventDto> processEventQueue(List<GameEvent> eventQueue) {
        return eventQueue.stream()
                .map(e -> new EventDto(getEventLabel(e), e))
                .toList();
    }

    private String getEventLabel(GameEvent event) {
        if (event instanceof BuildingUpgradeEvent) {
            return UPGRADE_EVENT;
        }
        if (event instanceof ArmyTrainingEvent) {
            return TRAINING_EVENT;
        }
        return "";
    }
}
