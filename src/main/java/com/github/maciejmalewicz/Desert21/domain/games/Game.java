package com.github.maciejmalewicz.Desert21.domain.games;

import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
@Document("current_games")
public class Game {

    @Id
    private String id;

    private List<Player> players;

    private Field[][] fields;

    private StateManager stateManager;

    public Game(List<Player> players, Field[][] fields, StateManager stateManager) {
        this.players = players;
        this.fields = fields;
        this.stateManager = stateManager;
    }

    private Field fieldAt(int row, int col) {
        return fields[row][col];
    }
}
