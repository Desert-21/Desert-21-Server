package com.github.maciejmalewicz.Desert21.domain.games;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Player {

    private String id;
    private String nickname;
    private ResourceSet resources;

    //beginning of the game, to be ignored later
    private Boolean isReady;

    public Player(String id, String nickname, ResourceSet resources) {
        this.id = id;
        this.nickname = nickname;
        this.resources = resources;
        this.isReady = false;
    }
}
