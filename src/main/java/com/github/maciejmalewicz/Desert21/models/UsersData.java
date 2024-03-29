package com.github.maciejmalewicz.Desert21.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.maciejmalewicz.Desert21.domain.users.FriendEntry;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record UsersData (
        String id,
        String nickname,
        List<FriendEntry> friends
) {
}
