package com.github.maciejmalewicz.Desert21.domain.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class FriendEntry {

    private String playerId;
    private String nickname;
}
