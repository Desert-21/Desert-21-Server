package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("current_games")
public class Game {

    @Id
    private String id;

    private List<Player> players;

    private Field[][] fields;
}
