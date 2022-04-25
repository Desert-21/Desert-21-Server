package com.github.maciejmalewicz.Desert21.domain.games;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.eventResults.EventResult;
import com.github.maciejmalewicz.Desert21.service.gameOrchestrator.turnExecution.gameEvents.GameEvent;
import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Data
@Document("current_games")
public class Game {

    @Id
    private String id;

    private List<Player> players;

    private Field[][] fields;

    private StateManager stateManager;

    private List<GameEvent> eventQueue;

    private List<EventResult> currentEventResults;

    public Game(List<Player> players, Field[][] fields, StateManager stateManager) {
        this.players = players;
        this.fields = fields;
        this.stateManager = stateManager;
        this.eventQueue = new ArrayList<>();
        this.currentEventResults = new ArrayList<>();
    }

    public Game(List<Player> players, Field[][] fields, StateManager stateManager, List<GameEvent> eventQueue) {
        this.players = players;
        this.fields = fields;
        this.stateManager = stateManager;
        this.eventQueue = eventQueue;
        this.currentEventResults = new ArrayList<>();
    }

    public Optional<Player> getCurrentPlayer() {
       return getPlayers().stream()
                .filter(p -> p.getId().equals(getStateManager().getCurrentPlayerId()))
                .findFirst();
    }

    public Optional<Player> getOtherPlayer() {
        return getPlayers().stream()
                .filter(p -> !p.getId().equals(getStateManager().getCurrentPlayerId()))
                .findFirst();
    }
}
